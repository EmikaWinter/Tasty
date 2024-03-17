package com.tms.an16.tasty.ui.recipes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tms.an16.tasty.databinding.ItemRecipesBinding
import com.tms.an16.tasty.model.Result

class RecipesAdapter :
    ListAdapter<Result, RecipesViewHolder>(object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.recipeId == newItem.recipeId
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder(
            ItemRecipesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}