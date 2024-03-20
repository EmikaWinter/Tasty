package com.tms.an16.tasty.ui.details.ingredients.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tms.an16.tasty.databinding.ItemIngredientsBinding
import com.tms.an16.tasty.model.ExtendedIngredient

class IngredientsAdapter : ListAdapter<ExtendedIngredient, IngredientsViewHolder>(object : DiffUtil.ItemCallback<ExtendedIngredient>(){
    override fun areItemsTheSame(
        oldItem: ExtendedIngredient,
        newItem: ExtendedIngredient
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: ExtendedIngredient,
        newItem: ExtendedIngredient
    ): Boolean {
        return false
    }

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        return  IngredientsViewHolder(
            ItemIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}