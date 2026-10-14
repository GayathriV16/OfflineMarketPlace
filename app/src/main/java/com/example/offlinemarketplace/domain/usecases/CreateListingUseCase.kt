package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.domain.repository.SyncRequestScheduler
import javax.inject.Inject

class CreateListingUseCase @Inject constructor(
    private val repository: ListingRepository,
    private val syncScheduler: SyncRequestScheduler
) {
    suspend operator fun invoke(draft: Listing) {
        val listing = Listing(
            id = System.currentTimeMillis(),
            title = draft.title,
            price = draft.price,
            category = draft.category,
            description = draft.description,
            imageUrl = draft.imageUrl,
            isFavorite = false
        )

        repository.createListingOffline(listing)
        syncScheduler.scheduleSync()
    }
}