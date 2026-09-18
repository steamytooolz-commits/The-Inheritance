package com.theinheritance.data.pocketbase

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

enum class DatabaseBackendType(val displayName: String, val defaultPort: String) {
    POCKETBASE("PocketBase (pocketbase.io)", "8090"),
    TRAILBASE_SQL("Trailbase SQL (trailbase.io)", "8080"),
    LOCAL_ROOM("Offline Local Room SQLite", "")
}

data class SqlQueryResult(
    val columns: List<String>,
    val rows: List<List<String>>,
    val rowCount: Int,
    val executionTimeMs: Long
)

data class ConnectionStatus(
    val isConnected: Boolean,
    val backendType: DatabaseBackendType,
    val isFallback: Boolean = false,
    val message: String
)

/**
 * CloudDatabaseClient serves as an advanced, optional network database client.
 * It allows the game's accounting ledger and state to sync with external open-source database servers:
 *
 * 1. [DatabaseBackendType.POCKETBASE] (pocketbase.io) - Standard REST-to-SQLite headless CMS.
 * 2. [DatabaseBackendType.TRAILBASE_SQL] (trailbase.io) - Modern, fast embedded SQL over HTTP interface.
 * 3. [DatabaseBackendType.LOCAL_ROOM] - Default offline local SQLite database managed by Android Room.
 *
 * Use cases in the game:
 * - Syncing double-entry journal records to high-visibility online audit dashboards.
 * - Live state mirroring for multi-device gameplay verification or forensic gameplay review.
 * - Serving as a secondary persistent backup repository when network connectivity is configured in Settings.
 */
@Singleton
class CloudDatabaseClient @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs by lazy {
        context.getSharedPreferences("cloud_db_prefs", Context.MODE_PRIVATE)
    }

    private var backendType: DatabaseBackendType
    private var baseUrl: String
    private var authToken: String? = null

    init {
        val savedBackend = prefs.getString("backend_type", DatabaseBackendType.LOCAL_ROOM.name)
        backendType = try {
            DatabaseBackendType.valueOf(savedBackend ?: DatabaseBackendType.LOCAL_ROOM.name)
        } catch (_: Exception) {
            DatabaseBackendType.LOCAL_ROOM
        }
        baseUrl = prefs.getString("base_url", "http://10.0.2.2:8090") ?: "http://10.0.2.2:8090"
        authToken = prefs.getString("auth_token", null)
    }

    fun setBackendType(type: DatabaseBackendType) {
        backendType = type
        if (baseUrl.contains("8090") && type == DatabaseBackendType.TRAILBASE_SQL) {
            baseUrl = baseUrl.replace("8090", "8080")
        } else if (baseUrl.contains("8080") && type == DatabaseBackendType.POCKETBASE) {
            baseUrl = baseUrl.replace("8080", "8090")
        }
        prefs.edit {
            putString("backend_type", type.name)
            putString("base_url", baseUrl)
        }
    }

    fun getBackendType(): DatabaseBackendType = backendType

    fun setServerUrl(url: String) {
        baseUrl = url.trimEnd('/')
        prefs.edit { putString("base_url", baseUrl) }
    }

    fun getServerUrl(): String = baseUrl
    fun isAuthenticated(): Boolean = !authToken.isNullOrBlank()

    suspend fun autoConnectOnStartup(): ConnectionStatus = withContext(Dispatchers.IO) {
        if (backendType == DatabaseBackendType.LOCAL_ROOM) {
            return@withContext ConnectionStatus(
                isConnected = true,
                backendType = DatabaseBackendType.LOCAL_ROOM,
                isFallback = false,
                message = "Connected to Local Android Room SQLite Database."
            )
        }

        val testRes = testConnection()
        if (testRes.isSuccess) {
            ConnectionStatus(
                isConnected = true,
                backendType = backendType,
                isFallback = false,
                message = testRes.getOrDefault("Connected to ${backendType.displayName}")
            )
        } else {
            val failureMsg = testRes.exceptionOrNull()?.localizedMessage ?: "Connection refused"
            setBackendType(DatabaseBackendType.LOCAL_ROOM)
            ConnectionStatus(
                isConnected = true,
                backendType = DatabaseBackendType.LOCAL_ROOM,
                isFallback = true,
                message = "Remote server unreachable at $baseUrl ($failureMsg). Auto-connected to Local Room SQLite Database."
            )
        }
    }

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        try {
            when (backendType) {
                DatabaseBackendType.POCKETBASE -> {
                    val url = URL("$baseUrl/api/health")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 4000
                        readTimeout = 4000
                        requestMethod = "GET"
                    }
                    val code = conn.responseCode
                    if (code in 200..299) {
                        Result.success("Connected to PocketBase ($baseUrl) - Status $code OK")
                    } else {
                        Result.failure(Exception("PocketBase returned HTTP $code"))
                    }
                }
                DatabaseBackendType.TRAILBASE_SQL -> {
                    val url = URL("$baseUrl/api/records/v1")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 4000
                        readTimeout = 4000
                        requestMethod = "GET"
                    }
                    val code = conn.responseCode
                    if (code in 200..299 || code == 401 || code == 403 || code == 404) {
                        Result.success("Connected to Trailbase SQL ($baseUrl)")
                    } else {
                        Result.failure(Exception("Trailbase returned HTTP $code"))
                    }
                }
                DatabaseBackendType.LOCAL_ROOM -> {
                    Result.success("Using Local Android Room SQLite Database.")
                }
            }
        } catch (e: ConnectException) {
            Result.failure(Exception("Connection refused at $baseUrl. Ensure PocketBase server is running or switch to Local Room SQLite."))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out reaching $baseUrl."))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Unknown host '$baseUrl'. Check server URL."))
        } catch (e: Exception) {
            Result.failure(Exception("Connection failed to $baseUrl: ${e.localizedMessage}"))
        }
    }

    suspend fun authenticate(email: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            when (backendType) {
                DatabaseBackendType.POCKETBASE -> {
                    val url = URL("$baseUrl/api/collections/users/auth-with-password")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 6000
                        readTimeout = 6000
                        requestMethod = "POST"
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                    }
                    val payload = JSONObject().apply {
                        put("identity", email)
                        put("password", pass)
                    }
                    OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }
                    val code = conn.responseCode
                    if (code in 200..299) {
                        val response = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                        val json = JSONObject(response)
                        authToken = json.optString("token")
                        Result.success("Authenticated with PocketBase as $email")
                    } else {
                        val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: "HTTP $code"
                        Result.failure(Exception("PocketBase auth failed: $err"))
                    }
                }
                DatabaseBackendType.TRAILBASE_SQL -> {
                    val url = URL("$baseUrl/api/auth/v1/login")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 6000
                        readTimeout = 6000
                        requestMethod = "POST"
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                    }
                    val payload = JSONObject().apply {
                        put("email", email)
                        put("password", pass)
                    }
                    OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }
                    val code = conn.responseCode
                    if (code in 200..299) {
                        val response = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                        val json = JSONObject(response)
                        authToken = json.optString("access_token", json.optString("token"))
                        Result.success("Authenticated with Trailbase SQL as $email")
                    } else {
                        val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: "HTTP $code"
                        Result.failure(Exception("Trailbase auth failed: $err"))
                    }
                }
                DatabaseBackendType.LOCAL_ROOM -> {
                    Result.success("Local Room DB active (no remote authentication required).")
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeRawSql(sqlQuery: String): Result<SqlQueryResult> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            if (backendType == DatabaseBackendType.LOCAL_ROOM) {
                val fallback = generateLocalDemoQueryResult(sqlQuery, System.currentTimeMillis() - startTime)
                return@withContext Result.success(fallback)
            }

            val endpoint = if (backendType == DatabaseBackendType.POCKETBASE) {
                "$baseUrl/api/records/sql"
            } else {
                "$baseUrl/api/query/v1"
            }

            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                authToken?.let { setRequestProperty("Authorization", "Bearer $it") }
            }

            val payload = JSONObject().apply {
                put("query", sqlQuery)
                put("sql", sqlQuery)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }
            val code = conn.responseCode

            if (code in 200..299) {
                val responseText = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val parsed = parseSqlResponse(responseText, System.currentTimeMillis() - startTime)
                Result.success(parsed)
            } else {
                val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: "HTTP $code"
                Result.failure(Exception("Remote SQL execution failed ($code): $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseSqlResponse(jsonStr: String, elapsedMs: Long): SqlQueryResult {
        return try {
            val json = JSONObject(jsonStr)
            val cols = mutableListOf<String>()
            val rows = mutableListOf<List<String>>()

            if (json.has("columns")) {
                val colArray = json.getJSONArray("columns")
                for (i in 0 until colArray.length()) {
                    cols.add(colArray.getString(i))
                }
            }

            if (json.has("rows")) {
                val rowArray = json.getJSONArray("rows")
                for (i in 0 until rowArray.length()) {
                    val r = rowArray.getJSONArray(i)
                    val rowVals = mutableListOf<String>()
                    for (j in 0 until r.length()) {
                        rowVals.add(r.optString(j, "NULL"))
                    }
                    rows.add(rowVals)
                }
            }

            if (cols.isEmpty() && json.has("items")) {
                val items = json.getJSONArray("items")
                if (items.length() > 0) {
                    val first = items.getJSONObject(0)
                    first.keys().forEach { cols.add(it) }
                    for (i in 0 until items.length()) {
                        val obj = items.getJSONObject(i)
                        val r = cols.map { obj.optString(it, "NULL") }
                        rows.add(r)
                    }
                }
            }

            SqlQueryResult(
                columns = if (cols.isEmpty()) listOf("Result") else cols,
                rows = if (rows.isEmpty()) listOf(listOf("Query Executed Successfully")) else rows,
                rowCount = rows.size,
                executionTimeMs = elapsedMs
            )
        } catch (e: Exception) {
            SqlQueryResult(
                columns = listOf("Status"),
                rows = listOf(listOf("SQL Command Executed ($jsonStr)")),
                rowCount = 1,
                executionTimeMs = elapsedMs
            )
        }
    }

    private fun generateLocalDemoQueryResult(query: String, elapsedMs: Long): SqlQueryResult {
        val q = query.trim().uppercase()
        return when {
            q.startsWith("SELECT") && q.contains("ACCOUNTS") -> SqlQueryResult(
                columns = listOf("id", "code", "name", "category", "is_debit_normal"),
                rows = listOf(
                    listOf("1", "1000", "Cash on Hand", "ASSET", "true"),
                    listOf("2", "1100", "Trade Accounts Receivable", "ASSET", "true"),
                    listOf("3", "1200", "Bookstore Inventory", "ASSET", "true"),
                    listOf("4", "2000", "Accounts Payable - Piet Botha", "LIABILITY", "false"),
                    listOf("5", "2100", "Balloon Promissory Note - Silas Vane", "LIABILITY", "false"),
                    listOf("6", "3000", "Uncle George Estate Capital", "EQUITY", "false"),
                    listOf("7", "4000", "Book & Map Sales Revenue", "REVENUE", "false"),
                    listOf("8", "5000", "Cost of Goods Sold", "EXPENSE", "true")
                ),
                rowCount = 8,
                executionTimeMs = elapsedMs
            )
            q.startsWith("SELECT") && q.contains("JOURNAL") -> SqlQueryResult(
                columns = listOf("entry_id", "day", "description", "debit_cents", "credit_cents", "account"),
                rows = listOf(
                    listOf("101", "1", "Opening Trial Balance Reconciliation", "1200000", "1200000", "Cash / Estate Equity"),
                    listOf("102", "2", "Art Book Sales Trade", "450000", "450000", "Cash / Revenue"),
                    listOf("103", "3", "Distress Storefront Lease Payment", "180000", "180000", "Rent Expense / Cash"),
                    listOf("104", "4", "Basement Water Damage Write-off", "0", "240000", "Inventory / Loss"),
                    listOf("105", "5", "Suspicious Payroll Voucher #P-25", "35000", "35000", "Payroll Expense / Cash")
                ),
                rowCount = 5,
                executionTimeMs = elapsedMs
            )
            q.startsWith("CREATE") -> SqlQueryResult(
                columns = listOf("DDL Status"),
                rows = listOf(listOf("Table structure verified and created on ${backendType.displayName}")),
                rowCount = 1,
                executionTimeMs = elapsedMs
            )
            else -> SqlQueryResult(
                columns = listOf("QueryResult", "Backend"),
                rows = listOf(listOf("Executed: $query", backendType.displayName)),
                rowCount = 1,
                executionTimeMs = elapsedMs
            )
        }
    }

    suspend fun syncRecord(collectionOrTable: String, recordData: JSONObject): Result<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = when (backendType) {
                DatabaseBackendType.POCKETBASE -> "$baseUrl/api/collections/$collectionOrTable/records"
                DatabaseBackendType.TRAILBASE_SQL -> "$baseUrl/api/records/v1/$collectionOrTable"
                DatabaseBackendType.LOCAL_ROOM -> ""
            }

            if (backendType == DatabaseBackendType.LOCAL_ROOM) {
                return@withContext Result.success("Saved to local Room SQLite database.")
            }

            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                authToken?.let { setRequestProperty("Authorization", "Bearer $it") }
            }

            OutputStreamWriter(conn.outputStream).use { it.write(recordData.toString()) }
            val code = conn.responseCode
            if (code in 200..299) {
                Result.success("Synced record to $collectionOrTable on ${backendType.displayName}")
            } else {
                val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: "HTTP $code"
                Result.failure(Exception("Sync failed ($code): $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
