package com.tms.an16.tasty.repository

import com.tms.an16.tasty.model.Trivia
import com.tms.an16.tasty.model.FoodRecipe
import com.tms.an16.tasty.network.Api
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: Api
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return api.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return api.searchRecipes(searchQuery)
    }

    suspend fun getTrivia(apiKey: String): Response<Trivia> {
        return api.getTrivia(apiKey)
    }

}