package com.tms.an16.tasty.util

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.tms.an16.tasty.R
import org.jsoup.Jsoup
import retrofit2.Response

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

fun parseHtml(textView: TextView, description: String?) {
    if (description != null) {
        textView.text = Jsoup.parse(description).text()
    }
}

fun applyVeganColor(view: View, vegan: Boolean) {
    if (vegan) {
        when (view) {
            is TextView -> {
                view.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.green
                    )
                )
            }

            is ImageView -> {
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        R.color.green
                    )
                )
            }
        }
    }
}

fun <T> handleResponse(response: Response<T>): NetworkResult<T> {
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

fun Context.isNetworkConnected(): Boolean {
    return (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.run {
        activeNetworkInfo?.isConnected == true
    } == true
}