package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.domain.repository.SyncRequestScheduler
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadInitialListingsUseCase @Inject constructor(
    private val repository: ListingRepository,
    private val syncRequestScheduler: SyncRequestScheduler
) {
    suspend operator fun invoke() {
        if (!repository.hasListings()) {
            val serverListings = repository.fetchListingsFromServer()
            repository.saveListings(serverListings)
        }

        if (repository.pendingOperationCount.first() > 0) {
            syncRequestScheduler.scheduleSync()
        }
    }
}