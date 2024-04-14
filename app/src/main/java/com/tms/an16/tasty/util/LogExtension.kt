package com.tms.an16.tasty.util

import android.util.Log
import com.tms.an16.tasty.BuildConfig


fun logError(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, message)
    }
}
