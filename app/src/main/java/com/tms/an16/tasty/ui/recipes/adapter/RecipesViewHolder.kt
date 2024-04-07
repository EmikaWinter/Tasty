package com.tms.an16.tasty.ui.recipes.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.databinding.ItemRecipesBinding
import com.tms.an16.tasty.util.applyVeganColor
import com.tms.an16.tasty.util.parseHtml

class RecipesViewHolder(private val binding: ItemRecipesBinding) : ViewHolder(binding.root) {

    fun bind(
        recipe: RecipeEntity,
        onClick: (recipe: RecipeEntity) -> Unit
    ) {
        binding.run {
            recipeImage.run {
                Glide.with(context).load(recipe.image).error(R.drawable.ic_empty_image).into(this)
            }
            titleTextView.text = recipe.title
            favTextView.text = recipe.aggregateLikes.toString()
            timeTextView.text = recipe.readyInMinutes.toString()

            parseHtml(this.descriptionTextView, recipe.summary)

            applyVeganColor(this.veganImageView, recipe.vegan)
            applyVeganColor(this.veganTextView, recipe.vegan)
        }

        binding.root.setOnClickListener {
            onClick(recipe)
        }
    }
}