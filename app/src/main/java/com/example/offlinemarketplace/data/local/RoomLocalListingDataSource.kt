
package com.example.offlinemarketplace.data.local

import androidx.room.withTransaction
import com.example.offlinemarketplace.sync.SyncOperationType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomLocalListingDataSource @Inject constructor(
    private val database: AppDatabase
) : LocalListingDataSource {

    private val listingDao = database.listingDao()
    private val syncOperationDao = database.syncOperationDao()

    override val pendingOperationCount: Flow<Int>
        get() = syncOperationDao.getPendingOperationCount()

    override fun observeListings(): Flow<List<ListingEntity>> =
        listingDao.getAllListings()

    override suspend fun saveListings(listings: List<ListingEntity>) {
        database.withTransaction {
            listingDao.insertListings(listings)
        }
    }

    override suspend fun createListingWithOperation(
        listing: ListingEntity,
        operation: SyncOperationEntity
    ) {
        database.withTransaction {
            listingDao.insertListings(listOf(listing))
            syncOperationDao.deleteForListing(listing.id)
            syncOperationDao.insert(operation)
        }
    }

    override suspend fun updateFavoriteAndQueue(
        listingId: Long,
        isFavorite: Boolean,
        updatedAt: Long
    ) {
        database.withTransaction {
            val updatedRows = listingDao.updateFavorite(
                listingId = listingId,
                isFavorite = isFavorite,
                updatedAt = updatedAt
            )

            if (updatedRows == 0) return@withTransaction

            val existing =
                syncOperationDao.getOperationForListing(listingId)

            if (existing?.operation != SyncOperationType.CREATE) {
                syncOperationDao.deleteForListing(listingId)
                syncOperationDao.insert(
                    SyncOperationEntity(
                        listingId = listingId,
                        operation = SyncOperationType.UPDATE,
                        createdAt = updatedAt
                    )
                )
            }
        }
    }

    override suspend fun updateListingAndQueue(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?,
        updatedAt: Long
    ) {
        database.withTransaction {
            val updatedRows = listingDao.updateListing(
                listingId = listingId,
                title = title,
                price = price,
                category = category,
                description = description,
                imageUri = imageUri,
                updatedAt = updatedAt
            )

            if (updatedRows == 0) return@withTransaction

            val existing =
                syncOperationDao.getOperationForListing(listingId)

            if (existing?.operation != SyncOperationType.CREATE) {
                syncOperationDao.deleteForListing(listingId)
                syncOperationDao.insert(
                    SyncOperationEntity(
                        listingId = listingId,
                        operation = SyncOperationType.UPDATE,
                        createdAt = updatedAt
                    )
                )
            }
        }
    }

    override suspend fun hasListings(): Boolean =
        listingDao.getListingCount() > 0

    override suspend fun getPendingOperations(): List<SyncOperationEntity> =
        syncOperationDao.getPendingOperations()

    override suspend fun getListingById(
        listingId: Long
    ): ListingEntity? =
        listingDao.getListingById(listingId)

    override suspend fun removeOperation(operationId: Long) {
        syncOperationDao.delete(operationId)
    }

    override suspend fun updateListingIfUnchanged(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUrl: String?,
        isFavorite: Boolean,
        serverUpdatedAt: Long,
        expectedUpdatedAt: Long
    ): Int =
        listingDao.updateListingIfUnchanged(
            listingId = listingId,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            serverUpdatedAt = serverUpdatedAt,
            expectedUpdatedAt = expectedUpdatedAt
        )

    override suspend fun completeSyncIfUnchanged(
        operationId: Long,
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUrl: String?,
        isFavorite: Boolean,
        serverUpdatedAt: Long,
        expectedUpdatedAt: Long
    ): Int = database.withTransaction {
        val updatedRows = listingDao.updateListingIfUnchanged(
            listingId = listingId,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            serverUpdatedAt = serverUpdatedAt,
            expectedUpdatedAt = expectedUpdatedAt
        )

        if (updatedRows == 1) {
            syncOperationDao.delete(operationId)
        }

        updatedRows
    }
}