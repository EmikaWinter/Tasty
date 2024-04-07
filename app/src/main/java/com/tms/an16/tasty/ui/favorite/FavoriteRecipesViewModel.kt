package com.tms.an16.tasty.ui.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val favoriteRecipes = MutableLiveData<List<FavoritesEntity>>()

    init {
        readFavoriteRecipes()
    }

    private fun readFavoriteRecipes() {
        viewModelScope.launch() {
            val recipes = withContext(Dispatchers.IO) {
                repository.local.readFavoriteRecipes()
            }
            favoriteRecipes.value = recipes
        }
    }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }
    }

    fun deleteAllFavoriteRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }
    }

}