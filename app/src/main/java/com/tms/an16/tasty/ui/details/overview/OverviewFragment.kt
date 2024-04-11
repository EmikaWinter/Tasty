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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.databinding.FragmentOverviewBinding
import com.tms.an16.tasty.util.Constants.Companion.RECIPE_RESULT_KEY
import com.tms.an16.tasty.util.parseHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        val recipeId = args?.getInt(RECIPE_RESULT_KEY) ?: 0

        viewModel.loadSelectedRecipeById(recipeId)

        viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            binding?.run {
                mainImageView.run {
                    Glide.with(requireContext())
                        .load(recipe.image)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .error(R.drawable.ic_empty_image)
                        .into(this)
                }

                titleTextView.text = recipe.title
                likesTextView.text = recipe.aggregateLikes.toString()
                timeTextView.text = recipe.readyInMinutes.toString()

                parseHtml(this.summaryTextView, recipe.summary)

                updateColors(recipe.vegetarian, this.vegetarianTextView)
                updateColors(recipe.vegan, this.veganTextView)
                updateColors(recipe.cheap, this.cheapTextView)
                updateColors(recipe.dairyFree, this.dairyTextView)
                updateColors(recipe.glutenFree, this.glutenFreeTextView)
                updateColors(recipe.veryHealthy, this.healthyTextView)
            }

            checkSavedRecipes(recipeId)

            binding?.saveToFavImageView?.setOnClickListener {
                if (!recipeSaved) {
                    saveToFavorites(recipe)
                } else {
                    deleteFromFavorites(recipe)
                }
            }
        }
    }

    private fun checkSavedRecipes(id: Int) {
        lifecycleScope.launch {
            viewModel.readFavoriteRecipes.collectLatest { favoritesEntity ->
                try {
                    for (savedRecipe in favoritesEntity) {
                        if (savedRecipe.recipeEntity.recipeId == id) {
                            setColorToSaveToFavImage(R.color.yellow)
                            savedRecipeId = savedRecipe.recipeEntity.recipeId
                            recipeSaved = true
                        }
                    }
                } catch (e: Exception) {
                    Log.d("OverviewFragment", e.message.toString())
                }
            }
        }
    }

    private fun saveToFavorites(recipeEntity: RecipeEntity) {
        val favoritesEntity = FavoritesEntity(recipeEntity, System.currentTimeMillis())
        viewModel.insertFavoriteRecipe(favoritesEntity)
        setColorToSaveToFavImage(R.color.yellow)
        showSnackBar(getString(R.string.recipe_saved))
        recipeSaved = true
    }

    private fun deleteFromFavorites(recipeEntity: RecipeEntity) {
        val favoritesEntity = FavoritesEntity(recipeEntity)
        viewModel.deleteFavoriteRecipe(favoritesEntity)
        setColorToSaveToFavImage(R.color.white)
        showSnackBar(getString(R.string.removed_from_favorites))
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).setAction(getString(R.string.OK)) {}
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
