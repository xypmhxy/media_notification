package com.nl.media.notification

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MediaServer : Service() {
    private var mServerBinder: MediaServerBinder? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        if (mServerBinder == null)
            mServerBinder = MediaServerBinder(service = this)
        Log.i("rqrq","onBind $mServerBinder")
        return mServerBinder
    }
}

class MediaServerBinder(var service: MediaServer? = null) : Binder()