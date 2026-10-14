package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.domain.repository.SyncRequestScheduler
import javax.inject.Inject

class UpdateListingUseCase @Inject constructor(
    private val repository: ListingRepository,
    private val syncScheduler: SyncRequestScheduler
) {
    suspend operator fun invoke(
        listingId: Long,
        draft: Listing
    ) {
        repository.updateListingOffline(
            listingId = listingId,
            title = draft.title,
            price = draft.price,
            category = draft.category,
            description = draft.description,
            imageUri = draft.imageUrl
        )
        syncScheduler.scheduleSync()
    }
}