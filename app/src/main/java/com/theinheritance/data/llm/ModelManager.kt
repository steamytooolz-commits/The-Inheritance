package com.theinheritance.data.llm

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

class ModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {
    fun downloadModel(model: LlmModel): UUID {
        val request = OneTimeWorkRequestBuilder<ModelDownloadWorker>()
            .setInputData(
                workDataOf(
                    "modelId" to model.id,
                    "url" to model.downloadUrl,
                    "sizeBytes" to model.sizeBytes
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()
            )
            .addTag("model_download")
            .build()

        workManager.enqueueUniqueWork(
            "download_${model.id}",
            ExistingWorkPolicy.KEEP,
            request
        )
        return request.id
    }

    fun observeProgress(workId: UUID): Flow<Int> =
        workManager.getWorkInfoByIdFlow(workId)
            .map { it?.progress?.getInt("progress", 0) ?: 0 }
}
