package com.tms.an16.tasty.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.toRecipeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val selectedRecipe = MutableLiveData<RecipeEntity>()

    fun loadSelectedRecipeById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedRecipe.postValue(repository.local.getSelectedRecipeById(id).toRecipeEntity())
        }
    }

    fun deleteAllSelectedRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllSelectedRecipes()

        }
    }
}