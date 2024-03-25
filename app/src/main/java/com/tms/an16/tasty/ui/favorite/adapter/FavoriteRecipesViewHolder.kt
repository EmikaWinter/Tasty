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
    ) {
        binding.run {
            recipeImage.run {
                Glide.with(context).load(favorite.result.image).into(this)
            }
            titleTextView.text = favorite.result.title
            favTextView.text = favorite.result.aggregateLikes.toString()
            timeTextView.text = favorite.result.readyInMinutes.toString()

            parseHtml(this.descriptionTextView, favorite.result.summary)

            applyVeganColor(this.veganImageView, favorite.result.vegan)
            applyVeganColor(this.veganTextView, favorite.result.vegan)
        }

        binding.root.setOnClickListener {
            onClick(favorite)
        }
    }
}