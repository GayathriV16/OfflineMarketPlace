package com.example.offlinemarketplace.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object SyncScheduler {

    fun scheduleSync(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "marketplace_sync",
                ExistingWorkPolicy.KEEP,
                syncRequest
            )
    }
}