package com.nl.media.notification.mediaSession

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent

class MediaSessionUser {

    private lateinit var mediaSession: MediaSessionCompat
    private var mOnMediaButtonListener: OnMediaButtonListener? = null

    companion object {
        const val MEDIA_SESSION_ACTIONS: Long = PlaybackStateCompat.ACTION_SEEK_TO

        val get by lazy(LazyThreadSafetyMode.NONE) {
            MediaSessionUser()
        }
    }

    fun initMediaSession(context: Context) {
        mediaSession = MediaSessionCompat(
            context,
            "MediaNotification"
        )

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {

            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                val keyEvent = if (Build.VERSION.SDK_INT >= 33) {
                    mediaButtonEvent?.getParcelableExtra<KeyEvent>(
                        Intent.EXTRA_KEY_EVENT,
                    )
                } else {
                    mediaButtonEvent?.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                }
                if (keyEvent?.action == KeyEvent.ACTION_UP) {
                    when (keyEvent.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PLAY -> mOnMediaButtonListener?.onClickPlay()
                        KeyEvent.KEYCODE_MEDIA_PAUSE -> mOnMediaButtonListener?.onClickPlay()
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> mOnMediaButtonListener?.onClickPrevious()
                        KeyEvent.KEYCODE_MEDIA_NEXT -> mOnMediaButtonListener?.onClickNext()
                    }
                }
                return true
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
            }
        })
        val playbackState =
            PlaybackStateCompat.Builder().setActions(MEDIA_SESSION_ACTIONS).build()
        mediaSession.setPlaybackState(playbackState)
        mediaSession.isActive = true
    }

    fun getSessionToken(): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }

    fun updatePlayState(isPlaying: Boolean?, position: Long?, duration: Long?, playSpeed: Float?) {
        val playingState = if (isPlaying == null) mediaSession.controller.playbackState.state else {
            if (isPlaying) PlaybackStateCompat.STATE_PLAYING else {
                PlaybackStateCompat.STATE_PAUSED
            }
        }

        val positionL = position ?: mediaSession.controller.playbackState.position

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder().setState(playingState, positionL, playSpeed ?: 1.0f)
                .build()
        )
        if (duration != null && duration > 0) {
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder().putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    duration
                ).build()
            )
        }
        android.util.Log.i("MusicMessageChannel", "position $position")
    }

    fun setOnMediaButtonListener(listener: OnMediaButtonListener) {
        mOnMediaButtonListener = listener;
    }
}

public interface OnMediaButtonListener {
    fun onClickPlay()
    fun onClickPrevious()
    fun onClickNext()
}