package com.theinheritance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theinheritance.data.local.converter.Converters
import com.theinheritance.data.local.dao.AccountDao
import com.theinheritance.data.local.dao.GameStateDao
import com.theinheritance.data.local.dao.GmMemoryDao
import com.theinheritance.data.local.dao.JournalDao
import com.theinheritance.data.local.dao.MarketEventDao
import com.theinheritance.data.local.dao.NpcDao
import com.theinheritance.data.local.entity.AccountEntity
import com.theinheritance.data.local.entity.FraudProfileEntity
import com.theinheritance.data.local.entity.GameStateEntity
import com.theinheritance.data.local.entity.GmMemoryEntity
import com.theinheritance.data.local.entity.JournalEntryEntity
import com.theinheritance.data.local.entity.JournalLineEntity
import com.theinheritance.data.local.entity.MarketEventEntity
import com.theinheritance.data.local.entity.NpcEntity
import com.theinheritance.data.local.entity.NpcMemoryEntity
import com.theinheritance.data.local.entity.RunHistoryEntity

@Database(
    entities = [
        AccountEntity::class, JournalEntryEntity::class, JournalLineEntity::class,
        GameStateEntity::class, NpcEntity::class, NpcMemoryEntity::class,
        GmMemoryEntity::class, MarketEventEntity::class, RunHistoryEntity::class,
        FraudProfileEntity::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InheritanceDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun journalDao(): JournalDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun npcDao(): NpcDao
    abstract fun gmMemoryDao(): GmMemoryDao
    abstract fun marketEventDao(): MarketEventDao
}
