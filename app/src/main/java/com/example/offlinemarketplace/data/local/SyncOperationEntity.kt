package com.example.offlinemarketplace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val listingId: Long,

    val operation: String,

    val createdAt: Long
)