package com.nl.media.notification.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.nl.media.notification.mediaSession.OnMediaButtonListener

class NotificationControlBroadcast : BroadcastReceiver() {
    private var mOnMediaButtonListener: OnMediaButtonListener? = null

    companion object {
        const val ACTION_NOTIFY = "notify"
        const val ACTION_PREV = "prev"
        const val ACTION_NEXT = "next"
        const val ACTION_CLICK_PLAY = "clickPlay"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        when (intent.action) {
            ACTION_PREV -> {
                mOnMediaButtonListener?.onClickPrevious()
            }

            ACTION_CLICK_PLAY -> {
                mOnMediaButtonListener?.onClickPlay()
            }

            ACTION_NEXT -> {
                mOnMediaButtonListener?.onClickNext()
            }
        }
    }


    fun setOnMediaButtonListener(listener: OnMediaButtonListener) {
        mOnMediaButtonListener = listener;
    }
}