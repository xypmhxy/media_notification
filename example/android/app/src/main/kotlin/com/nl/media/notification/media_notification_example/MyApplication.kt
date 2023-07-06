package com.nl.media.notification.media_notification_example

import io.flutter.app.FlutterApplication
import androidx.multidex.MultiDex

class MyApplication : FlutterApplication() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}