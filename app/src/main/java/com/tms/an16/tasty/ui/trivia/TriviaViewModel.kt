package com.tms.an16.tasty.ui.trivia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.controller.NetworkState
import com.tms.an16.tasty.database.entity.TriviaEntity
import com.tms.an16.tasty.model.Trivia
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TriviaViewModel @Inject constructor(
    private val repository: Repository,
    networkController: NetworkController
) : ViewModel() {

    var triviaResponse: MutableLiveData<NetworkResult<Trivia>> = MutableLiveData()

    val readTrivia: Flow<List<TriviaEntity>> = repository.local.readTrivia()

    private val isNetworkConnected = MutableLiveData<NetworkState>()

    init {
        viewModelScope.launch {
            networkController.isNetworkConnected.collectLatest {
                isNetworkConnected.value = it
            }
        }
    }

    fun getTrivia(apiKey: String) {
        viewModelScope.launch {
            triviaResponse.value = NetworkResult.Loading()
            if (isNetworkConnected.value == NetworkState.CONNECTED) {
                try {
                    val response = repository.remote.getTrivia(apiKey)
                    triviaResponse.value = handleTriviaResponse(response)

                    val trivia = triviaResponse.value?.data
                    if (trivia != null) {
                        offlineCacheTrivia(trivia)
                    }
                } catch (e: Exception) {
                    triviaResponse.value = NetworkResult.Error(messageId = R.string.data_not_found)
                }
            } else {
                triviaResponse.value = NetworkResult.Error(messageId = R.string.no_internet_connection)
            }
        }
    }

    private fun handleTriviaResponse(response: Response<Trivia>): NetworkResult<Trivia> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(messageId = R.string.timeout)
            }

            response.code() == 402 -> {
                NetworkResult.Error(messageId = R.string.api_key_limited)
            }

            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }

            else -> {
                NetworkResult.Error(message = response.message())
            }
        }
    }

    private fun offlineCacheTrivia(trivia: Trivia) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertTrivia(TriviaEntity(trivia))
        }
    }
}