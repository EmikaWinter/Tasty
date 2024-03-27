package com.tms.an16.tasty.ui.trivia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.database.entity.TriviaEntity
import com.tms.an16.tasty.model.Trivia
import com.tms.an16.tasty.repository.DataStoreRepository
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TriviaViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var triviaResponse: MutableLiveData<NetworkResult<Trivia>> = MutableLiveData()

    val readTrivia: LiveData<List<TriviaEntity>> = repository.local.readTrivia().asLiveData()

    fun getTrivia(apiKey: String) {
        viewModelScope.launch {
            triviaResponse.value = NetworkResult.Loading()
            if (dataStoreRepository.hasInternetConnection()) {
                try {
                    val response = repository.remote.getTrivia(apiKey)
                    triviaResponse.value = handleTriviaResponse(response)

                    val trivia = triviaResponse.value!!.data
                    if (trivia != null) {
                        offlineCacheTrivia(trivia)
                    }
                } catch (e: Exception) {
                    triviaResponse.value = NetworkResult.Error("Recipes not found.")
                }
            } else {
                triviaResponse.value = NetworkResult.Error("No Internet Connection.")
            }
        }
    }

    private fun handleTriviaResponse(response: Response<Trivia>): NetworkResult<Trivia> {
//        return handleResponse(response)
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }

            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }

            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }

            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun offlineCacheTrivia(trivia: Trivia) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertTrivia(TriviaEntity(trivia))
        }
    }
}