package com.tms.an16.tasty.ui.details

import androidx.lifecycle.ViewModel
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    suspend fun getById(id: Int): RecipeEntity {
        return repository.local.getRecipeById(id)
    }
}