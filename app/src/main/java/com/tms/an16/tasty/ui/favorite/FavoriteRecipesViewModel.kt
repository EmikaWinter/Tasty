package com.tms.an16.tasty.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.SelectedRecipeEntity
import com.tms.an16.tasty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()


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

    fun saveAndReplaceSelectedRecipe(selectedRecipeEntity: SelectedRecipeEntity) {
        viewModelScope.launch {
            repository.local.saveSelectedRecipe(selectedRecipeEntity)
        }
    }

}