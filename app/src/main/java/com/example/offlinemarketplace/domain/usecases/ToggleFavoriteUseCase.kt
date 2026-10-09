package com.example.offlinemarketplace.domain.usecases

import android.content.Context
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncScheduler

class ToggleFavoriteUseCase(
    private val repository: ListingRepository,
    private val context: Context
) {
    suspend operator fun invoke(
        listingId: Long,
        isFavorite: Boolean
    ) {
        repository.updateFavorite(
            listingId = listingId,
            isFavorite = isFavorite
        )
        SyncScheduler.scheduleSync(context)
    }
}
