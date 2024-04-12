package com.tms.an16.tasty.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tms.an16.tasty.controller.NetworkController
import com.tms.an16.tasty.controller.NetworkState
import com.tms.an16.tasty.util.isNetworkConnected
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NetworkReceiver : BroadcastReceiver() {

    @Inject
    lateinit var networkController: NetworkController

    private var scope: CoroutineScope? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        val isNetworkConnected: Boolean = context?.isNetworkConnected() == true
        scope = CoroutineScope(Dispatchers.Main)
        scope?.launch {
            if (isNetworkConnected) {
                networkController.isNetworkConnected.emit(NetworkState.CONNECTED)
            } else {
                networkController.isNetworkConnected.emit(NetworkState.DISCONNECTED)
            }
        }
    }
}
