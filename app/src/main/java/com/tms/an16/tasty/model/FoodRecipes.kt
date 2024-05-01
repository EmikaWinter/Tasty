package com.tms.an16.tasty.model

import com.google.gson.annotations.SerializedName

data class FoodRecipes(
    @SerializedName("results")
    val recipes: List<Recipe>
)