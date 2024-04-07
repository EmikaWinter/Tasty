package com.tms.an16.tasty.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tms.an16.tasty.util.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
data class FavoritesEntity(
    var recipeEntity: RecipeEntity
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}