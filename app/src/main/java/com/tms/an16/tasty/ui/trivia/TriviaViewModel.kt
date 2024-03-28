package com.tms.an16.tasty.ui.trivia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.database.entity.TriviaEntity
import com.tms.an16.tasty.model.Trivia
import com.tms.an16.tasty.repository.Repository
import com.tms.an16.tasty.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TriviaViewModel @Inject constructor(
    private val repository: Repository,
    networkController: NetworkController
) : ViewModel() {

    var triviaResponse: MutableLiveData<NetworkResult<Trivia>> = MutableLiveData()

    val readTrivia: LiveData<List<TriviaEntity>> = repository.local.readTrivia().asLiveData()

    private val isNetworkConnected = MutableLiveData<Boolean>()

    init {
        networkController.isNetworkConnected.subscribe {
            isNetworkConnected.value = it
        }
    }

    fun getTrivia(apiKey: String) {
        viewModelScope.launch {
            triviaResponse.value = NetworkResult.Loading()
            if (isNetworkConnected.value == true) {
                try {
                    val response = repository.remote.getTrivia(apiKey)
                    triviaResponse.value = handleTriviaResponse(response)

                    val trivia = triviaResponse.value!!.data
                    if (trivia != null) {
                        offlineCacheTrivia(trivia)
                    }
                } catch (e: Exception) {
                    triviaResponse.value = NetworkResult.Error("Data not found.")
                }
            } else {
                triviaResponse.value = NetworkResult.Error("No Internet Connection.")
            }
        }
    }

    private fun handleTriviaResponse(response: Response<Trivia>): NetworkResult<Trivia> {
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