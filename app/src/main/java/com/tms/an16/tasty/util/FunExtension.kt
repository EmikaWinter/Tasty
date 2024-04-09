@file:Suppress("DEPRECATION")

package com.tms.an16.tasty.util

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.database.entity.SelectedRecipeEntity
import org.jsoup.Jsoup

fun parseHtml(textView: TextView, description: String?) {
    if (description != null) {
        textView.text = Jsoup.parse(description).text()
    }
}

fun applyVeganColor(view: View, vegan: Boolean) {
    if (vegan) {
        when (view) {
            is TextView -> {
                view.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.green
                    )
                )
            }

            is ImageView -> {
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        R.color.green
                    )
                )
            }
        }
    }
}

fun Context.isNetworkConnected(): Boolean {
    return (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.run {
        activeNetworkInfo?.isConnected == true
    } == true
}

fun RecipeEntity.toSelectedRecipeEntity(): SelectedRecipeEntity {
    return SelectedRecipeEntity(
        recipeId,
        aggregateLikes,
        cheap,
        dairyFree,
        extendedIngredients,
        glutenFree,
        image,
        readyInMinutes,
        sourceName,
        sourceUrl,
        summary,
        title,
        vegan,
        vegetarian,
        veryHealthy,
    )
}

fun FavoritesEntity.toSelectedRecipeEntity(): SelectedRecipeEntity {
    return SelectedRecipeEntity(
        recipeEntity.recipeId,
        recipeEntity.aggregateLikes,
        recipeEntity.cheap,
        recipeEntity.dairyFree,
        recipeEntity.extendedIngredients,
        recipeEntity.glutenFree,
        recipeEntity.image,
        recipeEntity.readyInMinutes,
        recipeEntity.sourceName,
        recipeEntity.sourceUrl,
        recipeEntity.summary,
        recipeEntity.title,
        recipeEntity.vegan,
        recipeEntity.vegetarian,
        recipeEntity.veryHealthy,
    )
}

fun SelectedRecipeEntity.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        recipeId,
        aggregateLikes,
        cheap,
        dairyFree,
        extendedIngredients,
        glutenFree,
        image,
        readyInMinutes,
        sourceName,
        sourceUrl,
        summary,
        title,
        vegan,
        vegetarian,
        veryHealthy,
    )
}