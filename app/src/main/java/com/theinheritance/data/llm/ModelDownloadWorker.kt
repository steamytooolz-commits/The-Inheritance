package com.theinheritance.data.llm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ModelDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val url = inputData.getString("url").orEmpty()
        val modelId = inputData.getString("modelId").orEmpty()
        val sizeBytes = inputData.getLong("sizeBytes", -1L)

        if (url.isBlank() || modelId.isBlank()) return Result.failure()

        val modelDir = File(applicationContext.filesDir, "models")
        if (!modelDir.exists()) {
            modelDir.mkdirs()
        }
        val tempFile = File(modelDir, "$modelId.tmp")
        val finalFile = File(modelDir, "$modelId.bin")

        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return Result.failure()
                val body = response.body ?: return Result.failure()
                val totalBytes = if (body.contentLength() > 0) body.contentLength() else sizeBytes

                body.byteStream().use { inputStream ->
                    FileOutputStream(tempFile).use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Long = 0
                        var lastProgress = 0

                        while (true) {
                            if (isStopped) {
                                tempFile.delete()
                                return Result.failure()
                            }
                            val read = inputStream.read(buffer)
                            if (read == -1) break
                            outputStream.write(buffer, 0, read)
                            bytesRead += read

                            if (totalBytes > 0) {
                                val progress = ((bytesRead * 100) / totalBytes).toInt().coerceIn(0, 100)
                                if (progress != lastProgress) {
                                    lastProgress = progress
                                    setProgressAsync(workDataOf("progress" to progress))
                                }
                            }
                        }
                    }
                }
            }

            if (tempFile.exists()) {
                if (finalFile.exists()) {
                    finalFile.delete()
                }
                if (tempFile.renameTo(finalFile)) {
                    return Result.success()
                }
            }
            return Result.failure()
        } catch (e: IOException) {
            tempFile.delete()
            return Result.failure()
        }
    }
}

