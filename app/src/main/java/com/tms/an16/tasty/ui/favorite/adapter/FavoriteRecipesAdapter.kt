package com.tms.an16.tasty.ui.favorite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.databinding.ItemRecipesBinding

class FavoriteRecipesAdapter(
    private val onClick: (favorite: FavoritesEntity) -> Unit,
    private val onLongClick: (favorite: FavoritesEntity) -> Unit,
) :
    ListAdapter<FavoritesEntity, FavoriteRecipesViewHolder>(object :
        DiffUtil.ItemCallback<FavoritesEntity>() {
        override fun areItemsTheSame(oldItem: FavoritesEntity, newItem: FavoritesEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FavoritesEntity,
            newItem: FavoritesEntity
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipesViewHolder {
        return FavoriteRecipesViewHolder(
            ItemRecipesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoriteRecipesViewHolder, position: Int) {
        holder.bind(getItem(position), onClick, onLongClick)
    }
}