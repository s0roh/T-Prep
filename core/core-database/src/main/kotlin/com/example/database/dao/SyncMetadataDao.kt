package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.SyncMetadataDBO
import com.example.database.models.SyncStatus

@Dao
interface SyncMetadataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncMetadata: SyncMetadataDBO)

    @Query("DELETE FROM sync_metadata WHERE deckId = :deckId AND cardId IS NULL")
    suspend fun deleteDeckSyncMetadata(deckId: Long)

    @Query("DELETE FROM sync_metadata WHERE deckId = :deckId AND cardId = :cardId")
    suspend fun deleteCardSyncMetadata(deckId: Long, cardId: Long)

    @Query("DELETE FROM sync_metadata WHERE deckId = :deckId AND cardId IS NULL AND status = :status")
    suspend fun deleteUpdatedDeckSyncMetadata(deckId: Long, status: SyncStatus = SyncStatus.UPDATED)

    @Query("DELETE FROM sync_metadata WHERE deckId = :deckId AND cardId = :cardId AND status = :status")
    suspend fun deleteUpdatedCardSyncMetadata(
        deckId: Long,
        cardId: Long,
        status: SyncStatus = SyncStatus.UPDATED
    )

    @Query("SELECT * FROM sync_metadata WHERE deckId = :deckId AND cardId IS NULL")
    suspend fun getDeckSyncMetadata(deckId: Long): List<SyncMetadataDBO>

    @Query("SELECT * FROM sync_metadata WHERE deckId = :deckId AND cardId = :cardId")
    suspend fun getCardSyncMetadata(deckId: Long, cardId: Long): List<SyncMetadataDBO>
}