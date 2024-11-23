package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.HistoryDao
import com.example.database.models.HistoryDBO
import com.example.database.utils.Converters

class TPrepDatabase internal constructor(private val database: TPrepRoomDatabase) {
    val historyDao: HistoryDao
        get() = database.historyDao()
}

@Database(
    entities = [HistoryDBO::class],
    version = 1
)
@TypeConverters(Converters::class)
internal abstract class TPrepRoomDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

fun TPrepDatabase(applicationContext: Context) : TPrepDatabase {
    val tPrepRoomDatabase =
        Room.databaseBuilder(
            checkNotNull(applicationContext.applicationContext),
            TPrepRoomDatabase::class.java,
            "prep_database"
        ).build()
    return TPrepDatabase(tPrepRoomDatabase)
}