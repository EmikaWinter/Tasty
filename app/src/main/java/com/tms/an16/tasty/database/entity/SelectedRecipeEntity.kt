package com.tms.an16.tasty.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tms.an16.tasty.model.ExtendedIngredient
import com.tms.an16.tasty.util.Constants.Companion.SELECTED_RECIPES_TABLE

@Entity(tableName = SELECTED_RECIPES_TABLE)
data class SelectedRecipeEntity(
    @PrimaryKey
    @SerializedName("id")
    val recipeId: Int,
    val aggregateLikes: Int,
    val cheap: Boolean,
    val dairyFree: Boolean,
    val extendedIngredients: List<ExtendedIngredient>,
    val glutenFree: Boolean,
    val image: String,
    val readyInMinutes: Int,
    val sourceName: String?,
    val sourceUrl: String,
    val summary: String,
    val title: String,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
)