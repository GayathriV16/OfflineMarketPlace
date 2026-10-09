package com.example.offlinemarketplace.domain.usecases

import android.content.Context
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncScheduler

class UpdateListingUseCase(
    private val repository: ListingRepository,
    private val context: Context
) {
    suspend operator fun invoke(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) {
        repository.updateListingOffline(
            listingId = listingId,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUri = imageUri
        )
        SyncScheduler.scheduleSync(context)
    }
}
