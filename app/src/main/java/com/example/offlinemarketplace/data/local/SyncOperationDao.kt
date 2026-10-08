package com.example.offlinemarketplace.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: SyncOperationEntity)

    @Query("SELECT * FROM sync_operations ORDER BY createdAt ASC")
    suspend fun getPendingOperations(): List<SyncOperationEntity>

    @Query("DELETE FROM sync_operations WHERE id = :operationId")
    suspend fun delete(operationId: Long)

    @Query("DELETE FROM sync_operations WHERE listingId = :listingId")
    suspend fun deleteForListing(listingId: Long)

    @Query("SELECT * FROM sync_operations WHERE listingId = :listingId LIMIT 1")
    suspend fun getOperationForListing(
        listingId: Long
    ): SyncOperationEntity?

    @Query("SELECT COUNT(*) FROM sync_operations")
    fun getPendingOperationCount(): Flow<Int>
}