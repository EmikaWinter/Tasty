package com.tms.an16.tasty.ui.recipes.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bumptech.glide.Glide
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.ItemRecipesBinding
import com.tms.an16.tasty.model.Result

class RecipesViewHolder(private val binding: ItemRecipesBinding) : ViewHolder(binding.root) {

    fun bind(result: Result) {

        binding.recipeImage.run {
            Glide.with(context).load(result.image).error(R.drawable.ic_empty_image).into(this)
        }

        binding.titleTextView.text = result.title
        binding.descriptionTextView.text = result.summary
        binding.favTextView.text = result.aggregateLikes.toString()
        binding.timeTextView.text = result.readyInMinutes.toString()

        applyVeganColor(binding.veganImageView, result.vegan)
        applyVeganColor(binding.veganTextView, result.vegan)

    }

    private fun applyVeganColor(view: View, vegan: Boolean) {
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
}