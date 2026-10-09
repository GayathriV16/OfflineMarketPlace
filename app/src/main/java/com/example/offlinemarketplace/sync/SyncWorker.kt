package com.example.offlinemarketplace.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncManager.syncPendingOperations()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync pending operations", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
    }
}