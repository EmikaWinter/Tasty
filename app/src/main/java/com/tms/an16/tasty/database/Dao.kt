package com.tms.an16.tasty.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tms.an16.tasty.database.entity.RecipesEntity
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.FoodJokeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM food_joke_table ORDER BY id ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}