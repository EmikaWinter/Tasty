package com.tms.an16.tasty.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.controller.NetworkState
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.model.FoodRecipes
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.repository.DataStoreRepository
import com.tms.an16.tasty.repository.MealAndDietType
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.tms.an16.tasty.util.Constants.Companion.QUERY_DIET
import com.tms.an16.tasty.util.Constants.Companion.QUERY_TYPE
import com.tms.an16.tasty.util.toRecipeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    networkController: NetworkController
) : ViewModel() {

    var recipesResponse = MutableLiveData<NetworkResult<FoodRecipes>>()

    val readRecipes: LiveData<List<RecipeEntity>> = repository.local.readRecipes().asLiveData()

    val isNetworkConnected = MutableLiveData<NetworkState>()

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    var backOnline = false

    val readBackOnline: Flow<Boolean> = dataStoreRepository.readBackOnline

    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipes>> = MutableLiveData()

    private lateinit var mealAndDiet: MealAndDietType

    init {
        viewModelScope.launch {
            networkController.isNetworkConnected.collectLatest {
                isNetworkConnected.value = it
            }
        }
    }

    fun getRecipes(queries: Map<String, String>) {
        viewModelScope.launch {
            recipesResponse.value = NetworkResult.Loading()
            if (isNetworkConnected.value == NetworkState.CONNECTED) {
                try {
                    val response = repository.remote.getRecipes(queries)
                    recipesResponse.value = handleFoodRecipesResponse(response)

                    val foodRecipe = recipesResponse.value?.data
                    if (foodRecipe != null) {
                        offlineCacheRecipes(foodRecipe)
                    }

                } catch (e: Exception) {
                    recipesResponse.value =
                        NetworkResult.Error(messageId = R.string.recipes_not_found)

                }
            } else {
                recipesResponse.value =
                    NetworkResult.Error(messageId = R.string.no_internet_connection)
            }
        }
    }

    fun searchRecipes(searchQuery: Map<String, String>) {
        viewModelScope.launch {
            searchedRecipesResponse.value = NetworkResult.Loading()
            if (isNetworkConnected.value == NetworkState.CONNECTED) {
                try {
                    val response = repository.remote.searchRecipes(searchQuery)
                    searchedRecipesResponse.value = handleFoodRecipesResponse(response)
                } catch (e: Exception) {
                    searchedRecipesResponse.value =
                        NetworkResult.Error(messageId = R.string.recipes_not_found)
                }
            } else {
                searchedRecipesResponse.value =
                    NetworkResult.Error(messageId = R.string.no_internet_connection)
            }
        }
    }

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            if (this@RecipesViewModel::mealAndDiet.isInitialized) {
                dataStoreRepository.saveMealAndDietType(
                    mealAndDiet.selectedMealType,
                    mealAndDiet.selectedMealTypeId,
                    mealAndDiet.selectedDietType,
                    mealAndDiet.selectedDietTypeId
                )
            }
        }

    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = dataStoreRepository.applyQueries()

        if (this@RecipesViewModel::mealAndDiet.isInitialized) {
            queries[QUERY_TYPE] = mealAndDiet.selectedMealType
            queries[QUERY_DIET] = mealAndDiet.selectedDietType
        } else {
            queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
            queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        }
        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        return dataStoreRepository.applySearchQuery(searchQuery)
    }

    fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    private fun offlineCacheRecipes(foodRecipes: FoodRecipes) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllRecipes()
            repository.local.insertRecipes(foodRecipes.recipes.map { it.toRecipeEntity() })
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipes>): NetworkResult<FoodRecipes> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(messageId = R.string.timeout)
            }

            response.code() == 402 -> {
                NetworkResult.Error(messageId = R.string.api_key_limited)
            }

            response.body()?.recipes.isNullOrEmpty() -> {
                NetworkResult.Error(messageId = R.string.recipes_not_found)
            }

            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }

            else -> {
                NetworkResult.Error(message = response.message())
            }
        }
    }
}