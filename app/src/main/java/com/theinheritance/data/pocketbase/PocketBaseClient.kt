package com.theinheritance.data.pocketbase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PocketBaseClient @Inject constructor() {

    private var baseUrl: String = "http://10.0.2.2:8090"
    private var authToken: String? = null

    fun setServerUrl(url: String) {
        baseUrl = url.trimEnd('/')
    }

    fun getServerUrl(): String = baseUrl
    fun isAuthenticated(): Boolean = !authToken.isNullOrBlank()

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/health")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }
            val code = conn.responseCode
            if (code in 200..299) {
                Result.success("Connected to PocketBase ($baseUrl) - Status $code OK")
            } else {
                Result.failure(Exception("PocketBase returned HTTP $code"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection failed: ${e.localizedMessage}"))
        }
    }

    suspend fun authenticate(email: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        try {
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
                Result.success("Authenticated successfully as $email")
            } else {
                val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: "HTTP $code"
                Result.failure(Exception("Auth failed: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncRecord(collection: String, recordData: JSONObject): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/collections/$collection/records")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                authToken?.let { setRequestProperty("Authorization", it) }
            }

            OutputStreamWriter(conn.outputStream).use { it.write(recordData.toString()) }
            val code = conn.responseCode
            if (code in 200..299) {
                Result.success("Synced to $collection")
            } else {
                val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: "HTTP $code"
                Result.failure(Exception("Failed syncing to $collection: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
