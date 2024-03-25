package com.tms.an16.tasty.ui.recipes

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.RecipesEntity
import com.tms.an16.tasty.model.FoodRecipe
import com.tms.an16.tasty.repository.DataStoreRepository
import com.tms.an16.tasty.repository.MealAndDietType
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.Constants.Companion.API_KEY
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.tms.an16.tasty.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.tms.an16.tasty.util.Constants.Companion.QUERY_API_KEY
import com.tms.an16.tasty.util.Constants.Companion.QUERY_DIET
import com.tms.an16.tasty.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.tms.an16.tasty.util.Constants.Companion.QUERY_NUMBER
import com.tms.an16.tasty.util.Constants.Companion.QUERY_SEARCH
import com.tms.an16.tasty.util.Constants.Companion.QUERY_TYPE
import com.tms.an16.tasty.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    var recipesResponse = MutableLiveData<NetworkResult<FoodRecipe>>()

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    private lateinit var mealAndDiet: MealAndDietType

    fun getRecipes(queries: Map<String, String>) {
        viewModelScope.launch {
            recipesResponse.value = NetworkResult.Loading()
            if (dataStoreRepository.hasInternetConnection()) {
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

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (dataStoreRepository.hasInternetConnection()) {
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
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

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
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    fun showNetworkStatus(context: Context) {
        if (!networkStatus) {
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

//    fun applyQueries(): HashMap<String, String> {
//        val queries: HashMap<String, String> = HashMap()
//
//        queries[QUERY_NUMBER] = "50"
//        queries[QUERY_API_KEY] = API_KEY
//        queries[QUERY_TYPE] = "snack"
//        queries[QUERY_DIET] = "vegan"
//        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
//        queries[QUERY_FILL_INGREDIENTS] = "true"
//
//        return queries
//    }
}