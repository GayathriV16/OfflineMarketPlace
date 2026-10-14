package com.example.offlinemarketplace.data.repository

import androidx.room.withTransaction
import com.example.offlinemarketplace.data.local.AppDatabase
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
    private val database: AppDatabase,
    private val listingApi: ListingApi
) : SyncRepository {

    private val syncMutex = Mutex()

    override suspend fun syncPendingOperations() {
        syncMutex.withLock {
            val listingDao = database.listingDao()
            val syncOperationDao = database.syncOperationDao()
            val operations = syncOperationDao.getPendingOperations()

            for (operation in operations) {
                val listing = listingDao.getListingById(operation.listingId)

                if (listing == null) {
                    database.withTransaction {
                        syncOperationDao.delete(operation.id)
                    }
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

                val rowsUpdated = database.withTransaction {
                    val updatedRows = listingDao.updateListingIfUnchanged(
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

                    if (updatedRows == 1) {
                        syncOperationDao.delete(operation.id)
                    }

                    updatedRows
                }

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