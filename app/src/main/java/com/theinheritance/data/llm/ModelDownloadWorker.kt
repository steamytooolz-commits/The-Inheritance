package com.theinheritance.data.llm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class ModelDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val url = inputData.getString("url").orEmpty()
        if (url.isBlank()) return Result.failure()
        for (p in 0..100 step 10) {
            setProgressAsync(workDataOf("progress" to p))
            try {
                Thread.sleep(120)
            } catch (_: InterruptedException) {
                return Result.failure()
            }
            if (isStopped) return Result.failure()
        }
        return Result.success()
    }
}
