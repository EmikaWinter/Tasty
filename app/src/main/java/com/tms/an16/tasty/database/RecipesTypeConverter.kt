package com.tms.an16.tasty.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.model.ExtendedIngredient

class RecipesTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun resultToString(result: RecipeEntity): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): RecipeEntity {
        val listType = object : TypeToken<RecipeEntity>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun ingredientsToString(result: List<ExtendedIngredient>): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToIngredients(data: String): List<ExtendedIngredient> {
        val listType = object : TypeToken<List<ExtendedIngredient>>() {}.type
        return gson.fromJson(data, listType)
    }
}