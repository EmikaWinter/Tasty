package com.tms.an16.tasty.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tms.an16.tasty.database.entity.RecipesEntity
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.FoodJokeEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FoodJokeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun getRecipesDao(): Dao

}