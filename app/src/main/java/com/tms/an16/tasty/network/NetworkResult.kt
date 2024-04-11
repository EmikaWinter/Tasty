package com.tms.an16.tasty.network

sealed class NetworkResult<T>(
    val data: T? = null,
    val messageId: Int? = null,
    val message: String? = null
) {

    class Success<T>(data: T): NetworkResult<T>(data)
    class Error<T>(messageId: Int? = null, message: String? = null, data: T? = null): NetworkResult<T>(data, messageId, message)
    class Loading<T>: NetworkResult<T>()

}