package com.example.offlinemarketplace.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.offlinemarketplace.AppContainer

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val appContainer = AppContainer(applicationContext)

            appContainer.syncManager.syncPendingOperations()

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}