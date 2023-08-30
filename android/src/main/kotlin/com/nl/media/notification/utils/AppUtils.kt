package com.nl.media.notification.utils

import android.os.Build

class AppUtils {
    companion object {
        fun isAndroid33(): Boolean {
            return Build.VERSION.SDK_INT >= 33;
        }
    }
}