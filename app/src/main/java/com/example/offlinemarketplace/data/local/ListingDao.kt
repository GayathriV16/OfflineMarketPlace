package com.example.offlinemarketplace.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {

    @Query("SELECT * FROM listings")
    fun getAllListings(): Flow<List<ListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<ListingEntity>)

    @Query(
        """
        UPDATE listings
        SET isFavorite = :isFavorite,
            needsSync = 1,
            updatedAt = :updatedAt
        WHERE id = :listingId
        """
    )
    suspend fun updateFavorite(
        listingId: Long,
        isFavorite: Boolean,
        updatedAt: Long
    ): Int

    @Query("DELETE FROM listings")
    suspend fun deleteAllListings()

    @Query("SELECT * FROM listings")
    suspend fun getAllListingsOnce(): List<ListingEntity>

    @Query(
        "SELECT * FROM listings WHERE id = :listingId LIMIT 1"
    )
    suspend fun getListingById(listingId: Long): ListingEntity?

    @Query(
        """
        UPDATE listings
        SET title = :title,
            price = :price,
            category = :category,
            description = :description,
            imageUrl = :imageUri,
            updatedAt = :updatedAt,
            needsSync = 1
        WHERE id = :listingId
        """
    )
    suspend fun updateListing(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?,
        updatedAt: Long
    ): Int

    @Query("SELECT COUNT(*) FROM listings")
    suspend fun getListingCount(): Int

    @Query(
        """
        UPDATE listings
        SET needsSync = 0,
            updatedAt = :updatedAt
        WHERE id = :listingId
        """
    )
    suspend fun markSynced(
        listingId: Long,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE listings
        SET title = :title,
            price = :price,
            category = :category,
            description = :description,
            imageUrl = :imageUrl,
            isFavorite = :isFavorite,
            updatedAt = :serverUpdatedAt,
            needsSync = 0
        WHERE id = :listingId
          AND updatedAt = :expectedUpdatedAt
        """
    )
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
}
