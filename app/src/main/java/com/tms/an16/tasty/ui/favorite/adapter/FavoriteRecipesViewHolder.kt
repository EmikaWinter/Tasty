package com.tms.an16.tasty.ui.favorite.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.databinding.ItemRecipesBinding
import com.tms.an16.tasty.util.applyVeganColor
import com.tms.an16.tasty.util.parseHtml

class FavoriteRecipesViewHolder(private val binding: ItemRecipesBinding) :
    ViewHolder(binding.root) {

    fun bind(
        favorite: FavoritesEntity,
        onClick: (favorite: FavoritesEntity) -> Unit,
        onLongClick: (favorite: FavoritesEntity) -> Unit,
    ) {
        binding.run {
            recipeImage.run {
                Glide.with(context).load(favorite.recipeEntity.image).into(this)
            }
            titleTextView.text = favorite.recipeEntity.title
            favTextView.text = favorite.recipeEntity.aggregateLikes.toString()
            timeTextView.text = favorite.recipeEntity.readyInMinutes.toString()

            parseHtml(this.descriptionTextView, favorite.recipeEntity.summary)

            applyVeganColor(this.veganImageView, favorite.recipeEntity.vegan)
            applyVeganColor(this.veganTextView, favorite.recipeEntity.vegan)
        }

        binding.root.setOnClickListener {
            onClick(favorite)
        }

        binding.root.setOnLongClickListener {
            onLongClick(favorite)
            true
        }
    }
}