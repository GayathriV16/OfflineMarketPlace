package com.example.offlinemarketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ListingEntity::class,
        SyncOperationEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(SyncOperationTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao

    abstract fun syncOperationDao(): SyncOperationDao
}