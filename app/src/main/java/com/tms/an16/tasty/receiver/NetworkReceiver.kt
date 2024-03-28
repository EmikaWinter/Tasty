package com.tms.an16.tasty.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.util.isNetworkConnected
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NetworkReceiver : BroadcastReceiver() {

    @Inject
    lateinit var networkController: NetworkController
    override fun onReceive(context: Context?, intent: Intent?) {
        val isNetworkConnected: Boolean = context?.isNetworkConnected() == true
        networkController.isNetworkConnected.onNext(isNetworkConnected)

//        val connectivityManager =
//            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//        if (networkInfo != null && networkInfo.isConnected) {
//            // internet connection is available
//        } else {
//            // internet connection is not available
//        }
    }
}