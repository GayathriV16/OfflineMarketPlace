package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.domain.repository.SyncRequestScheduler
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ListingRepository,
    private val syncScheduler: SyncRequestScheduler
) {
    suspend operator fun invoke(
        listingId: Long,
        isFavorite: Boolean
    ) {
        repository.updateFavorite(
            listingId = listingId,
            isFavorite = isFavorite
        )
        syncScheduler.scheduleSync()
    }
}