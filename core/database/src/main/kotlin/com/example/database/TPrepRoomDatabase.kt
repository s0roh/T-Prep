package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.CardDao
import com.example.database.dao.DeckDao
import com.example.database.dao.HistoryDao
import com.example.database.dao.SyncMetadataDao
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO
import com.example.database.models.HistoryDBO
import com.example.database.models.SyncMetadataDBO
import com.example.database.utils.Converters

class TPrepDatabase internal constructor(private val database: TPrepRoomDatabase) {
    val historyDao: HistoryDao
        get() = database.historyDao()

    val deckDao: DeckDao
        get() = database.deckDao()

    val cardDao: CardDao
        get() = database.cardDao()

    val syncMetadataDao: SyncMetadataDao
        get() = database.syncMetadataDao()
}

@Database(
    entities = [
        CardDBO::class,
        DeckDBO::class,
        HistoryDBO::class,
        SyncMetadataDBO::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
internal abstract class TPrepRoomDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}

fun TPrepDatabase(applicationContext: Context): TPrepDatabase {
    val tPrepRoomDatabase =
        Room.databaseBuilder(
            checkNotNull(applicationContext.applicationContext),
            TPrepRoomDatabase::class.java,
            "prep_database"
        ).build()
    return TPrepDatabase(tPrepRoomDatabase)
}