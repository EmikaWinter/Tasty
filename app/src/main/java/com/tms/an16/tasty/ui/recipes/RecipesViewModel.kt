package com.tms.an16.tasty.ui.recipes

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.database.entity.RecipesEntity
import com.tms.an16.tasty.model.FoodRecipe
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.repository.DataStoreRepository
import com.tms.an16.tasty.repository.MealAndDietType
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.tms.an16.tasty.util.Constants.Companion.QUERY_DIET
import com.tms.an16.tasty.util.Constants.Companion.QUERY_TYPE
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

    var recipesResponse = MutableLiveData<NetworkResult<FoodRecipe>>()

    val readRecipes: Flow<List<RecipesEntity>> = repository.local.readRecipes()

    val isNetworkConnected = MutableLiveData<Boolean>()

    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    val readBackOnline: Flow<Boolean> = dataStoreRepository.readBackOnline

    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

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
            if (isNetworkConnected.value == true) {
                try {
                    val response = repository.remote.getRecipes(queries)
                    recipesResponse.value = handleFoodRecipesResponse(response)

                    val foodRecipe = recipesResponse.value!!.data
                    if (foodRecipe != null) {
                        offlineCacheRecipes(foodRecipe)
                    }

                } catch (e: Exception) {
                    recipesResponse.value = NetworkResult.Error("Recipes not found.")
                }
            } else {
                recipesResponse.value = NetworkResult.Error("No Internet Connection.")
            }
        }
    }

    fun searchRecipes(searchQuery: Map<String, String>) {
        viewModelScope.launch {
            searchedRecipesResponse.value = NetworkResult.Loading()
            if (isNetworkConnected.value == true) {
                try {
                    val response = repository.remote.searchRecipes(searchQuery)
                    searchedRecipesResponse.value = handleFoodRecipesResponse(response)
                } catch (e: java.lang.Exception) {
                    searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
                }
            } else {
                searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
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

    fun showNetworkStatus(context: Context) {
        if (isNetworkConnected.value != true) {
            Toast.makeText(context, "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else {
            if (backOnline) {
                Toast.makeText(context, "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipe(RecipesEntity(foodRecipe))
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }

            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }

            response.body()!!.results.isEmpty() -> {
                NetworkResult.Error("Recipes not found.")
            }

            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }

            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }
}