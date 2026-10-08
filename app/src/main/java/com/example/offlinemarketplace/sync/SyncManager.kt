package com.example.offlinemarketplace.sync

import com.example.offlinemarketplace.data.local.ListingDao
import com.example.offlinemarketplace.data.local.ListingEntity
import com.example.offlinemarketplace.data.local.SyncOperationDao
import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.ListingApi

class SyncManager(
    private val listingDao: ListingDao,
    private val syncOperationDao: SyncOperationDao,
    private val listingApi: ListingApi
) {

    suspend fun syncPendingOperations() {

        val operations =
            syncOperationDao.getPendingOperations()

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
                updatedAt = listing.updatedAt
            )

            val serverListing = when (operation.operation) {

                "CREATE" -> {
                    listingApi.createListing(apiListing)
                }

                "UPDATE" -> {
                    listingApi.updateListing(apiListing)
                }

                else -> {
                    null
                }
            }

            if (serverListing != null) {

                listingDao.insertListings(
                    listOf(
                        ListingEntity(
                            id = serverListing.id,
                            title = serverListing.title,
                            price = serverListing.price,
                            category = serverListing.category,
                            description = serverListing.description,
                            imageUrl = serverListing.imageUrl,
                            isFavorite = listing.isFavorite,
                            updatedAt = serverListing.updatedAt,
                            needsSync = false
                        )
                    )
                )

                syncOperationDao.delete(operation.id)
            }
        }
    }
}