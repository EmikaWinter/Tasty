package com.tms.an16.tasty.controller

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkController @Inject constructor() {

    val isNetworkConnected = MutableStateFlow(NetworkState.UNKNOWN)

}

enum class NetworkState {
    CONNECTED,
    DISCONNECTED,
    UNKNOWN
}