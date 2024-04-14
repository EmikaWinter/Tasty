package com.tms.an16.tasty.repository

import com.tms.an16.tasty.database.Dao
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.database.entity.TriviaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: Dao
) {

    fun readRecipes(): Flow<List<RecipeEntity>> {
        return recipesDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    fun readTrivia(): Flow<List<TriviaEntity>> {
        return recipesDao.readTrivia()
    }

    suspend fun insertRecipes(list: List<RecipeEntity>) {
        recipesDao.insertRecipes(list)
    }

    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun insertTrivia(triviaEntity: TriviaEntity) {
        recipesDao.insertTrivia(triviaEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllRecipes() {
        recipesDao.deleteAllRecipes()
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }
}