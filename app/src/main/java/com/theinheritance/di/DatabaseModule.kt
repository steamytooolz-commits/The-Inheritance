package com.theinheritance.di

import android.content.Context
import androidx.room.Room
import com.theinheritance.data.local.InheritanceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): InheritanceDatabase =
        Room.databaseBuilder(ctx, InheritanceDatabase::class.java, "inheritance.db")
            .fallbackToDestructiveMigration().build()

    @Provides fun provideAccountDao(db: InheritanceDatabase) = db.accountDao()
    @Provides fun provideJournalDao(db: InheritanceDatabase) = db.journalDao()
    @Provides fun provideGameStateDao(db: InheritanceDatabase) = db.gameStateDao()
    @Provides fun provideNpcDao(db: InheritanceDatabase) = db.npcDao()
    @Provides fun provideGmMemoryDao(db: InheritanceDatabase) = db.gmMemoryDao()
    @Provides fun provideMarketEventDao(db: InheritanceDatabase) = db.marketEventDao()
}
