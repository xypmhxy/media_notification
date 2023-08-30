package com.nl.media.notification.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nl.media.notification.mediaSession.OnMediaButtonListener

class NotificationCommandReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_PREVIOUS = "action_mn_previous"
        const val ACTION_PLAY_PAUSE = "action_mn_play"
        const val ACTION_NEXT = "action_mn_next"
    }

    private var mOnMediaButtonListener: OnMediaButtonListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || intent.action == null) return
        val action = intent.action
        if (ACTION_PREVIOUS == action) {
            mOnMediaButtonListener?.onClickPrevious()
        } else if (ACTION_PLAY_PAUSE == action) {
            mOnMediaButtonListener?.onClickPlay()
        } else if (ACTION_NEXT == action) {
            mOnMediaButtonListener?.onClickNext()
        }
    }

    fun setOnMediaButtonListener(listener: OnMediaButtonListener) {
        mOnMediaButtonListener = listener;
    }
}