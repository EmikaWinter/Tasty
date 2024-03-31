package com.tms.an16.tasty.ui.details.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val readFavoriteRecipes: Flow<List<FavoritesEntity>> =
        repository.local.readFavoriteRecipes()

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipe(favoritesEntity)
        }
    }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }
    }
}