package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncScheduler
import android.content.Context

class CreateListingUseCase(
    private val repository: ListingRepository,
    private val context: Context
) {
    suspend operator fun invoke(listing: Listing) {
        repository.createListingOffline(listing)
        SyncScheduler.scheduleSync(context)
    }
}
