package com.example.offlinemarketplace.di

import android.content.Context
import androidx.room.Room
import com.example.offlinemarketplace.data.local.AppDatabase
import com.example.offlinemarketplace.data.local.ListingDao
import com.example.offlinemarketplace.data.local.SyncOperationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "offline_marketplace.db"
        ).build()
    }

    @Provides
    fun provideListingDao(
        database: AppDatabase
    ): ListingDao = database.listingDao()

    @Provides
    fun provideSyncOperationDao(
        database: AppDatabase
    ): SyncOperationDao = database.syncOperationDao()
}
