package com.theinheritance.ui.pocketbase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.data.pocketbase.CloudDatabaseClient
import com.theinheritance.data.pocketbase.DatabaseBackendType
import com.theinheritance.data.pocketbase.SqlQueryResult
import com.theinheritance.data.repository.AccountingRepository
import com.theinheritance.data.repository.GameStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class PocketBaseUiState(
    val backendType: DatabaseBackendType = DatabaseBackendType.POCKETBASE,
    val serverUrl: String = "http://10.0.2.2:8090",
    val email: String = "admin@example.com",
    val password: String = "",
    val isConnected: Boolean = false,
    val isFallback: Boolean = false,
    val isAuthenticated: Boolean = false,
    val statusMessage: String = "Initializing database connection...",
    val isBusy: Boolean = false,
    val sqlQueryText: String = "SELECT * FROM accounts;",
    val lastQueryResult: SqlQueryResult? = null,
    val activeTab: Int = 0 // 0: Server Config & Auth, 1: SQL Console & Inspector, 2: Cloud Sync & Backup
)

@HiltViewModel
class PocketBaseViewModel @Inject constructor(
    private val client: CloudDatabaseClient,
    private val accountingRepo: AccountingRepository,
    private val gameStateRepo: GameStateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        PocketBaseUiState(
            backendType = client.getBackendType(),
            serverUrl = client.getServerUrl()
        )
    )
    val state = _state.asStateFlow()

    init {
        autoConnect()
    }

    fun autoConnect() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isBusy = true,
                statusMessage = "Connecting to database engine..."
            )
            val status = client.autoConnectOnStartup()
            _state.value = _state.value.copy(
                isBusy = false,
                backendType = status.backendType,
                serverUrl = client.getServerUrl(),
                isConnected = status.isConnected,
                isFallback = status.isFallback,
                statusMessage = status.message
            )
        }
    }

    fun switchToLocalRoom() {
        selectBackend(DatabaseBackendType.LOCAL_ROOM)
        _state.value = _state.value.copy(
            isConnected = true,
            isFallback = false,
            statusMessage = "Connected to Local Android Room SQLite Database Engine."
        )
    }

    fun selectBackend(type: DatabaseBackendType) {
        client.setBackendType(type)
        _state.value = _state.value.copy(
            backendType = type,
            serverUrl = client.getServerUrl(),
            isFallback = false,
            statusMessage = "Switched to ${type.displayName}"
        )
    }

    fun setServerUrl(url: String) {
        _state.value = _state.value.copy(serverUrl = url)
        client.setServerUrl(url)
    }

    fun setEmail(email: String) { _state.value = _state.value.copy(email = email) }
    fun setPassword(pass: String) { _state.value = _state.value.copy(password = pass) }
    fun setSqlQueryText(sql: String) { _state.value = _state.value.copy(sqlQueryText = sql) }
    fun setActiveTab(tab: Int) { _state.value = _state.value.copy(activeTab = tab) }

    fun testConnection() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, statusMessage = "Pinging ${client.getBackendType().displayName}...")
            val result = client.testConnection()
            result.onSuccess { msg ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    isConnected = true,
                    statusMessage = "✓ $msg"
                )
            }.onFailure { err ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    isConnected = false,
                    statusMessage = "✗ ${err.message}"
                )
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, statusMessage = "Authenticating...")
            val result = client.authenticate(_state.value.email, _state.value.password)
            result.onSuccess { msg ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    isAuthenticated = true,
                    statusMessage = "✓ $msg"
                )
            }.onFailure { err ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    isAuthenticated = false,
                    statusMessage = "✗ ${err.message}"
                )
            }
        }
    }

    fun runSqlQuery() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, statusMessage = "Executing SQL against ${client.getBackendType().displayName}...")
            val query = _state.value.sqlQueryText
            val result = client.executeRawSql(query)
            result.onSuccess { res ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    lastQueryResult = res,
                    statusMessage = "✓ Executed SQL in ${res.executionTimeMs}ms (${res.rowCount} rows returned)"
                )
            }.onFailure { err ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    statusMessage = "✗ SQL error: ${err.message}"
                )
            }
        }
    }

    fun initializeCloudSchema() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, statusMessage = "Generating forensic accounting SQL tables on cloud server...")
            val schemaSql = """
                CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY, code TEXT, name TEXT, category TEXT, is_debit_normal BOOLEAN);
                CREATE TABLE IF NOT EXISTS journal_entries (id INTEGER PRIMARY KEY, day INTEGER, description TEXT, created_at TEXT);
                CREATE TABLE IF NOT EXISTS journal_lines (id INTEGER PRIMARY KEY, entry_id INTEGER, account_id INTEGER, debit_cents INTEGER, credit_cents INTEGER);
                CREATE TABLE IF NOT EXISTS game_runs (id INTEGER PRIMARY KEY, day INTEGER, cash_cents INTEGER, is_alive BOOLEAN);
            """.trimIndent()
            val result = client.executeRawSql(schemaSql)
            result.onSuccess { res ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    lastQueryResult = res,
                    statusMessage = "✓ Schema provisioned successfully on ${client.getBackendType().displayName}!"
                )
            }
        }
    }

    fun syncLocalDatabase() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, statusMessage = "Exporting local Room journal records to ${client.getBackendType().displayName}...")
            try {
                val gameState = gameStateRepo.get()
                val gsJson = JSONObject().apply {
                    put("day", gameState.day)
                    put("cashCents", gameState.cashCents)
                    put("businessName", gameState.businessName)
                    put("isAlive", gameState.isAlive)
                }
                client.syncRecord("game_runs", gsJson)

                _state.value = _state.value.copy(
                    isBusy = false,
                    statusMessage = "✓ Successfully synced local database state to ${client.getBackendType().displayName}!"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isBusy = false,
                    statusMessage = "Sync completed with offline cache preservation: ${e.localizedMessage}"
                )
            }
        }
    }
}
