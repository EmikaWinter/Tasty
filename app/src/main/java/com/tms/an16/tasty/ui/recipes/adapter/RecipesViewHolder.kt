package com.tms.an16.tasty.ui.recipes.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.ItemRecipesBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.util.applyVeganColor
import com.tms.an16.tasty.util.parseHtml

class RecipesViewHolder(private val binding: ItemRecipesBinding) : ViewHolder(binding.root) {

    fun bind(
        result: Result,
        onClick: (result: Result) -> Unit
    ) {
        binding.run {
            recipeImage.run {
                Glide.with(context).load(result.image).error(R.drawable.ic_empty_image).into(this)
            }
            titleTextView.text = result.title
            favTextView.text = result.aggregateLikes.toString()
            timeTextView.text = result.readyInMinutes.toString()

            parseHtml(this.descriptionTextView, result.summary)

            applyVeganColor(this.veganImageView, result.vegan)
            applyVeganColor(this.veganTextView, result.vegan)
        }

        binding.root.setOnClickListener {
            onClick(result)
        }
    }
}