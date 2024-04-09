package com.tms.an16.tasty.network

import com.tms.an16.tasty.model.Trivia
import com.tms.an16.tasty.model.FoodRecipes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface Api {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipes>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipes>

    @GET("food/trivia/random")
    suspend fun getTrivia(
        @Query("apiKey") apiKey: String
    ): Response<Trivia>
}