package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.sync.SyncManager

class SyncListingsUseCase(
    private val syncManager: SyncManager
) {
    suspend operator fun invoke() {
        syncManager.syncPendingOperations()
    }
}
