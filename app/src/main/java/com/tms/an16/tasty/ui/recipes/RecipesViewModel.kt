package com.tms.an16.tasty.ui.recipes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.model.FoodRecipe
import com.tms.an16.tasty.repository.DataStoreRepository
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    var recipesResponse = MutableLiveData<NetworkResult<FoodRecipe>>()

    fun getRecipes(queries: Map<String, String>) {
        viewModelScope.launch {
            recipesResponse.value = NetworkResult.Loading()
            if (dataStoreRepository.hasInternetConnection()) {
                try {
                    val response = repository.remote.getRecipes(queries)
                    recipesResponse.value = handleFoodRecipesResponse(response)


                } catch (e: Exception) {
                    recipesResponse.value = NetworkResult.Error("Recipes not found.")
                }
            } else {
                recipesResponse.value = NetworkResult.Error("No Internet Connection.")
            }
        }

    }


    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                return NetworkResult.Success(response.body()!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }
}