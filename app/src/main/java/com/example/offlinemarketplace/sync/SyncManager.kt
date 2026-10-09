
package com.example.offlinemarketplace.sync

import com.example.offlinemarketplace.data.local.ListingDao
import com.example.offlinemarketplace.data.local.SyncOperationDao
import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.ListingApi

class SyncManager(
    private val listingDao: ListingDao,
    private val syncOperationDao: SyncOperationDao,
    private val listingApi: ListingApi
) {

    suspend fun syncPendingOperations() {
        val operations = syncOperationDao.getPendingOperations()

        for (operation in operations) {
            val listing =
                listingDao.getListingById(operation.listingId)
                    ?: continue

            val apiListing = ApiListing(
                id = listing.id,
                title = listing.title,
                price = listing.price,
                category = listing.category,
                description = listing.description,
                imageUrl = listing.imageUrl,
                updatedAt = listing.updatedAt,
                isFavorite = listing.isFavorite
            )

            val serverListing = when (operation.operation) {
                SyncOperationType.CREATE ->
                    listingApi.createListing(apiListing)

                SyncOperationType.UPDATE ->
                    listingApi.updateListing(apiListing)

                else -> continue
            }

            val rowsUpdated = listingDao.updateListingIfUnchanged(
                listingId = listing.id,
                title = serverListing.title,
                price = serverListing.price,
                category = serverListing.category,
                description = serverListing.description,
                imageUrl = serverListing.imageUrl,
                isFavorite = serverListing.isFavorite,
                serverUpdatedAt = serverListing.updatedAt,
                expectedUpdatedAt = listing.updatedAt
            )

            // Delete only after the local listing was updated successfully.
            // If a newer edit exists, leave the operation pending for retry.
            if (rowsUpdated == 1) {
                syncOperationDao.delete(operation.id)
            }
        }
    }
}