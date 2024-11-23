package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("entityId") val entityId: Long,
    @ColumnInfo("entityType") val entityType: EntityType,
    @ColumnInfo("status") val status: SyncStatus,
    @ColumnInfo("lastSynced") val lastSynced: Long
)

enum class EntityType {
    DECK,
    CARD
}

enum class SyncStatus {
    NEW,
    UPDATED,
    DELETED
}

