
package com.example.offlinemarketplace.domain.repository

import com.example.offlinemarketplace.domain.model.Listing
import kotlinx.coroutines.flow.Flow

interface ListingRepository {
    val pendingOperationCount: Flow<Int>

    fun getListings(): Flow<List<Listing>>

    suspend fun saveListings(listings: List<Listing>)

    suspend fun createListingOffline(listing: Listing)

    suspend fun updateFavorite(
        listingId: Long,
        isFavorite: Boolean
    )

    suspend fun updateListingOffline(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    )

    suspend fun hasListings(): Boolean

    suspend fun fetchListingsFromServer(): List<Listing>
}