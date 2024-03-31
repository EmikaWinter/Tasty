package com.tms.an16.tasty.ui.details.overview

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.databinding.FragmentOverviewBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.util.Constants.Companion.RECIPE_RESULT_KEY
import com.tms.an16.tasty.util.parseHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class OverviewFragment : Fragment() {

    private var binding: FragmentOverviewBinding? = null

    private val viewModel: OverviewViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val recipeResult = args?.getParcelable<Result>(RECIPE_RESULT_KEY)

        val favoritesEntity =
            FavoritesEntity(
                0,
                recipeResult as Result
            )

        binding?.run {
            mainImageView.run {
                Glide.with(requireContext()).load(recipeResult.image).into(this)
            }
            titleTextView.text = recipeResult.title
            likesTextView.text = recipeResult.aggregateLikes.toString()
            timeTextView.text = recipeResult.readyInMinutes.toString()

            parseHtml(this.summaryTextView, recipeResult.summary)

            updateColors(recipeResult.vegetarian, this.vegetarianTextView)
            updateColors(recipeResult.vegan, this.veganTextView)
            updateColors(recipeResult.cheap, this.cheapTextView)
            updateColors(recipeResult.dairyFree, this.dairyTextView)
            updateColors(recipeResult.glutenFree, this.glutenFreeTextView)
            updateColors(recipeResult.veryHealthy, this.healthyTextView)
        }

        checkSavedRecipes(recipeResult.recipeId)

        binding?.saveToFavImageView?.setOnClickListener {

            if (!recipeSaved) {
                saveToFavorites(favoritesEntity)
            } else {
                deleteFromFavorites(recipeResult)
            }
        }
    }

    private fun checkSavedRecipes(id: Int) {
        lifecycleScope.launch {
            viewModel.readFavoriteRecipes.collectLatest { favoritesEntity ->
                try {
                    for (savedRecipe in favoritesEntity) {
                        if (savedRecipe.result.recipeId == id) {
                            setColorToSaveToFavImage(R.color.yellow)
                            savedRecipeId = savedRecipe.id
                            recipeSaved = true
                        }
                    }
                } catch (e: Exception) {
                    Log.d("OverviewFragment", e.message.toString())
                }
            }
        }
    }

    private fun saveToFavorites(favoritesEntity: FavoritesEntity) {
        viewModel.insertFavoriteRecipe(favoritesEntity)
        setColorToSaveToFavImage(R.color.yellow)
        showSnackBar("Recipe saved")
        recipeSaved = true
    }

    private fun deleteFromFavorites(recipeResult: Result) {
        val favoritesEntity = FavoritesEntity(savedRecipeId, recipeResult)
        viewModel.deleteFavoriteRecipe(favoritesEntity)
        setColorToSaveToFavImage(R.color.white)
        showSnackBar("Removed from Favorites")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    private fun setColorToSaveToFavImage(color: Int) {
        binding?.run {
            saveToFavImageView.setColorFilter(
                getColor(this.saveToFavImageView.context, color)
            )
        }
    }

    private fun updateColors(state: Boolean, textView: TextView) {
        if (state) {
            textView.setTextColor(getColor(requireContext(), R.color.green))
            textView.setDrawableTintColor(R.color.green)
        }
    }

    private fun TextView.setDrawableTintColor(colorRes: Int) {
        for (drawable in this.compoundDrawablesRelative) {
            drawable?.colorFilter =
                PorterDuffColorFilter(getColor(context, colorRes), PorterDuff.Mode.SRC_IN)
        }
    }
}
