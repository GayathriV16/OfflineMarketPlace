
package com.example.offlinemarketplace.data.repository

import androidx.room.withTransaction
import com.example.offlinemarketplace.data.local.AppDatabase
import com.example.offlinemarketplace.data.local.ListingEntity
import com.example.offlinemarketplace.data.local.SyncOperationEntity
import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.ListingApi
import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncOperationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListingRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val listingApi: ListingApi
) : ListingRepository {

    private val listingDao = database.listingDao()
    private val syncOperationDao = database.syncOperationDao()

    override val pendingOperationCount: Flow<Int> =
        syncOperationDao.getPendingOperationCount()

    override fun getListings(): Flow<List<Listing>> {
        return listingDao.getAllListings().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun saveListings(listings: List<Listing>) {
        database.withTransaction {
            listingDao.insertListings(
                listings.map { listing -> listing.toEntity() }
            )
        }
    }

    override suspend fun createListingOffline(listing: Listing) {
        val now = System.currentTimeMillis()

        val entity = ListingEntity(
            id = listing.id,
            title = listing.title,
            price = listing.price,
            category = listing.category,
            description = listing.description,
            imageUrl = listing.imageUrl,
            isFavorite = listing.isFavorite,
            updatedAt = now,
            needsSync = true
        )

        database.withTransaction {
            listingDao.insertListings(listOf(entity))
            syncOperationDao.deleteForListing(listing.id)
            syncOperationDao.insert(
                SyncOperationEntity(
                    listingId = listing.id,
                    operation = SyncOperationType.CREATE,
                    createdAt = now
                )
            )
        }
    }

    override suspend fun updateFavorite(
        listingId: Long,
        isFavorite: Boolean
    ) {
        val now = System.currentTimeMillis()

        database.withTransaction {
            val rowsUpdated = listingDao.updateFavorite(
                listingId = listingId,
                isFavorite = isFavorite,
                updatedAt = now
            )

            if (rowsUpdated == 0) return@withTransaction

            val existingOperation =
                syncOperationDao.getOperationForListing(listingId)

            if (existingOperation?.operation != SyncOperationType.CREATE) {
                syncOperationDao.deleteForListing(listingId)
                syncOperationDao.insert(
                    SyncOperationEntity(
                        listingId = listingId,
                        operation = SyncOperationType.UPDATE,
                        createdAt = now
                    )
                )
            }
        }
    }

    override suspend fun updateListingOffline(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) {
        val now = System.currentTimeMillis()

        database.withTransaction {
            val rowsUpdated = listingDao.updateListing(
                listingId = listingId,
                title = title,
                price = price,
                category = category,
                description = description,
                imageUri = imageUri,
                updatedAt = now
            )

            if (rowsUpdated == 0) return@withTransaction

            val existingOperation =
                syncOperationDao.getOperationForListing(listingId)

            if (existingOperation?.operation != SyncOperationType.CREATE) {
                syncOperationDao.deleteForListing(listingId)
                syncOperationDao.insert(
                    SyncOperationEntity(
                        listingId = listingId,
                        operation = SyncOperationType.UPDATE,
                        createdAt = now
                    )
                )
            }
        }
    }

    override suspend fun hasListings(): Boolean {
        return listingDao.getListingCount() > 0
    }

    override suspend fun fetchListingsFromServer(): List<Listing> {
        return listingApi.getListings().map { apiListing ->
            apiListing.toDomain()
        }
    }

    private fun Listing.toEntity(): ListingEntity {
        return ListingEntity(
            id = id,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            updatedAt = System.currentTimeMillis(),
            needsSync = false
        )
    }

    private fun ListingEntity.toDomain(): Listing {
        return Listing(
            id = id,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite
        )
    }

    private fun ApiListing.toDomain(): Listing {
        return Listing(
            id = id,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite
        )
    }
}