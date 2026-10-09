package com.example.offlinemarketplace.domain.usecases

import android.content.Context
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncScheduler
import kotlinx.coroutines.flow.first

class LoadInitialListingsUseCase(
    private val repository: ListingRepository,
    private val context: Context
) {
    suspend operator fun invoke() {
        if (!repository.hasListings()) {
            val serverListings = repository.fetchListingsFromServer()
            repository.saveListings(serverListings)
        }
        if (repository.pendingOperationCount.first() > 0) {
            SyncScheduler.scheduleSync(context)
        }
    }
}
