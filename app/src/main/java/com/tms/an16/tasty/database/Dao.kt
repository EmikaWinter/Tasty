package com.tms.an16.tasty.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.database.entity.TriviaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(list: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrivia(triviaEntity: TriviaEntity)

    @Query("SELECT * FROM recipes_table ORDER BY title ASC")
    fun readRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY saveTimestamp DESC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM trivia_table ORDER BY id ASC")
    fun readTrivia(): Flow<List<TriviaEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM recipes_table")
    suspend fun deleteAllRecipes()

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}