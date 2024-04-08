package com.tms.an16.tasty.ui.details.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.toRecipeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    val readFavoriteRecipes: Flow<List<FavoritesEntity>> = repository.local.readFavoriteRecipes()

    val selectedRecipe = MutableLiveData<RecipeEntity>()

    fun loadSelectedRecipeById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedRecipe.postValue(repository.local.getSelectedRecipeById(id).toRecipeEntity())
        }
    }

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