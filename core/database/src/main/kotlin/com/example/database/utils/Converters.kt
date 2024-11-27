package com.example.database.utils

import androidx.room.TypeConverter
import com.example.database.models.CardStatus
import com.example.database.models.DeckStatus
import com.example.database.models.EntityType
import com.example.database.models.Source
import com.example.database.models.SyncStatus

internal class Converters {

    // Для DeckStatus
    @TypeConverter
    fun fromDeckStatus(status: DeckStatus): String {
        return status.name
    }

    @TypeConverter
    fun toDeckStatus(value: String): DeckStatus {
        return DeckStatus.valueOf(value)
    }

    // Для CardStatus
    @TypeConverter
    fun fromCardStatus(status: CardStatus): String {
        return status.name
    }

    @TypeConverter
    fun toCardStatus(value: String): CardStatus {
        return CardStatus.valueOf(value)
    }

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