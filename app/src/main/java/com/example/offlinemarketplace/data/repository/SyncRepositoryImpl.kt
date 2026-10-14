
package com.example.offlinemarketplace.data.repository

import com.example.offlinemarketplace.data.local.LocalListingDataSource
import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.ListingApi
import com.example.offlinemarketplace.domain.repository.SyncRepository
import com.example.offlinemarketplace.sync.SyncOperationType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val localDataSource: LocalListingDataSource,
    private val listingApi: ListingApi
) : SyncRepository {

    private val syncMutex = Mutex()

    override suspend fun syncPendingOperations() {
        syncMutex.withLock {
            val operations = localDataSource.getPendingOperations()

            for (operation in operations) {
                val listing = localDataSource.getListingById(
                    operation.listingId
                )

                if (listing == null) {
                    localDataSource.removeOperation(operation.id)
                    continue
                }

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
                }

                val rowsUpdated =
                    localDataSource.completeSyncIfUnchanged(
                        operationId = operation.id,
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

                if (rowsUpdated == 0) {
                    throw SyncPendingException(
                        "Listing ${listing.id} changed during synchronization"
                    )
                }
            }
        }
    }
}

class SyncPendingException(message: String) : Exception(message)