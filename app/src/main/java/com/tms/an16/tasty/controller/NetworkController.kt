package com.tms.an16.tasty.controller

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkController @Inject constructor() {

    val isNetworkConnected = MutableStateFlow(true)

//    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        connectivityManager.registerDefaultNetworkCallback(this)
//
//        val network =
//            connectivityManager.activeNetwork
//        if (network == null) {
//            isNetworkConnected.value = false
//            return isNetworkConnected
//        }
//
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
//        if (networkCapabilities == null) {
//            isNetworkConnected.value = false
//            return isNetworkConnected
//        }
//
//        return when {
//            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
//                isNetworkConnected.value = true
//                isNetworkConnected
//            }
//            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
//                isNetworkConnected.value = true
//                isNetworkConnected
//            }
//            else -> {
//                isNetworkConnected.value = false
//                isNetworkConnected
//            }
//        }
//    }
//
//    override fun onAvailable(network: Network) {
//        isNetworkConnected.value = true
//    }
//
//    override fun onLost(network: Network) {
//        isNetworkConnected.value = false
//    }
}