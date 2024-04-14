@file:Suppress("DEPRECATION")

package com.tms.an16.tasty.util

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.model.Recipe
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

fun Recipe.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        orderId = 0,
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
