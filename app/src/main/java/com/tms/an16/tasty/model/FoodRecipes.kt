package com.tms.an16.tasty.model

import com.google.gson.annotations.SerializedName
import com.tms.an16.tasty.database.entity.RecipeEntity

data class FoodRecipes(
    @SerializedName("results")
    val recipes: List<RecipeEntity>
)