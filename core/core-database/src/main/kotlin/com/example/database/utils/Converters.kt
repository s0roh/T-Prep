package com.example.database.utils

import androidx.room.TypeConverter
import com.example.database.models.EntityType
import com.example.database.models.Source
import com.example.database.models.SyncStatus

internal class Converters {

    // Для SyncStatus
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }

    // Для EntityType
    @TypeConverter
    fun fromEntityType(entityType: EntityType): String {
        return entityType.name
    }

    @TypeConverter
    fun toEntityType(value: String): EntityType {
        return EntityType.valueOf(value)
    }

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(source: String): Source {
        return Source.valueOf(source)
    }
}