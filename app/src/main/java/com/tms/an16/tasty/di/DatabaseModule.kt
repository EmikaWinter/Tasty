package com.tms.an16.tasty.di

import android.content.Context
import androidx.room.Room
import com.tms.an16.tasty.database.Dao
import com.tms.an16.tasty.database.RecipesDatabase
import com.tms.an16.tasty.util.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RecipesDatabase {
        return Room.databaseBuilder(
            context,
            RecipesDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase): Dao {
        return database.getRecipesDao()
    }
}