package com.example.offlinemarketplace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val price: Double,
    val category: String,
    val description: String,
    val imageUrl: String?,
    val isFavorite: Boolean,
    // Used for conflict resolution
    val updatedAt: Long,
    // True when this listing has local changes
    val needsSync: Boolean
)