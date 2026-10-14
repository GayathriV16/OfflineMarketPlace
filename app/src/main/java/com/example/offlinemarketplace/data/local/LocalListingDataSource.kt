package com.example.offlinemarketplace.data.local

import kotlinx.coroutines.flow.Flow

interface LocalListingDataSource {

    val pendingOperationCount: Flow<Int>

    fun observeListings(): Flow<List<ListingEntity>>

    suspend fun saveListings(listings: List<ListingEntity>)

    suspend fun createListingWithOperation(
        listing: ListingEntity,
        operation: SyncOperationEntity
    )

    suspend fun updateFavoriteAndQueue(
        listingId: Long,
        isFavorite: Boolean,
        updatedAt: Long
    )

    suspend fun updateListingAndQueue(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?,
        updatedAt: Long
    )

    suspend fun hasListings(): Boolean

    suspend fun getPendingOperations(): List<SyncOperationEntity>

    suspend fun getListingById(
        listingId: Long
    ): ListingEntity?

    suspend fun removeOperation(operationId: Long)

    suspend fun updateListingIfUnchanged(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUrl: String?,
        isFavorite: Boolean,
        serverUpdatedAt: Long,
        expectedUpdatedAt: Long
    ): Int

    suspend fun completeSyncIfUnchanged(
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
    ): Int
}
