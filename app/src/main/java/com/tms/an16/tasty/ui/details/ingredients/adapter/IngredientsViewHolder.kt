package com.tms.an16.tasty.ui.details.ingredients.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.ItemIngredientsBinding
import com.tms.an16.tasty.model.ExtendedIngredient
import com.tms.an16.tasty.util.Constants.Companion.BASE_IMAGE_URL
import java.util.Locale

class IngredientsViewHolder(private val binding: ItemIngredientsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(ingredients: ExtendedIngredient) {

        binding.ingredientImageView.run {
            Glide.with(context).load(BASE_IMAGE_URL + ingredients.image)
                .error(R.drawable.ic_empty_image).into(this)
        }

        binding.ingredientName.text = ingredients.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }

        binding.ingredientAmount.text = ingredients.amount.toString()
        binding.ingredientUnit.text = ingredients.unit
        binding.ingredientConsistency.text = ingredients.consistency
        binding.ingredientOriginal.text = ingredients.original
    }
}