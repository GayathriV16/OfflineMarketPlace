package com.example.offlinemarketplace.data.local

import androidx.room.TypeConverter
import com.example.offlinemarketplace.sync.SyncOperationType

class SyncOperationTypeConverter {

    @TypeConverter
    fun fromOperationType(value: SyncOperationType): String {
        return value.name
    }

    @TypeConverter
    fun toOperationType(value: String): SyncOperationType {
        return SyncOperationType.valueOf(value)
    }
}