package com.example.offlinemarketplace.data.repository

import com.example.offlinemarketplace.data.local.ListingDao
import com.example.offlinemarketplace.data.local.ListingEntity
import com.example.offlinemarketplace.data.local.SyncOperationDao
import com.example.offlinemarketplace.data.local.SyncOperationEntity
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.ListingApi
import com.example.offlinemarketplace.sync.SyncOperationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListingRepository(
    private val listingDao: ListingDao,
    private val syncOperationDao: SyncOperationDao,
    private val listingApi: ListingApi
) {

    val pendingOperationCount: Flow<Int> =
        syncOperationDao.getPendingOperationCount()

    fun getListings(): Flow<List<Listing>> {
        return listingDao.getAllListings()
            .map { entities ->
                entities.map { it.toListing() }
            }
    }

    suspend fun saveListings(listings: List<Listing>) {
        listingDao.insertListings(
            listings.map { it.toEntity() }
        )
    }

    suspend fun createListingOffline(listing: Listing) {
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

    suspend fun updateFavorite(
        listingId: Long,
        isFavorite: Boolean
    ) {
        val now = System.currentTimeMillis()

        val rowsUpdated = listingDao.updateFavorite(
            listingId = listingId,
            isFavorite = isFavorite,
            updatedAt = now
        )

        if (rowsUpdated == 0) return

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

    private fun ListingEntity.toListing(): Listing {
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

    suspend fun updateListingOffline(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) {
        val now = System.currentTimeMillis()

        val rowsUpdated = listingDao.updateListing(
            listingId = listingId,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUri = imageUri,
            updatedAt = now
        )

        if (rowsUpdated == 0) return

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

    suspend fun hasListings(): Boolean {
        return listingDao.getListingCount() > 0
    }

    suspend fun fetchListingsFromServer(): List<Listing> {
        return listingApi.getListings()
            .map { it.toListing() }
    }

    private fun ApiListing.toListing(): Listing {
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