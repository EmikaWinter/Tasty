package com.tms.an16.tasty.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tms.an16.tasty.model.FoodRecipe
import com.tms.an16.tasty.util.Constants.Companion.RECIPES_TABLE


@Entity(tableName = RECIPES_TABLE)
data class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}