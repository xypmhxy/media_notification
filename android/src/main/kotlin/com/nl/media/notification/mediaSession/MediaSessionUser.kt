package com.nl.media.notification.mediaSession

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent

class MediaSessionUser {

    private lateinit var mediaSession: MediaSessionCompat
    private var mOnMediaButtonListener: OnMediaButtonListener? = null

    companion object {
        const val MEDIA_SESSION_ACTIONS: Long = PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT


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

//            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
//                val keyEvent = if (Build.VERSION.SDK_INT >= 33) {
//                    mediaButtonEvent?.getParcelableExtra<KeyEvent>(
//                        Intent.EXTRA_KEY_EVENT,
//                    )
//                } else {
//                    mediaButtonEvent?.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
//                }
//                if (keyEvent?.action == KeyEvent.ACTION_UP) {
//                    when (keyEvent.keyCode) {
//                        KeyEvent.KEYCODE_MEDIA_PLAY -> mOnMediaButtonListener?.onClickPlay()
//                        KeyEvent.KEYCODE_MEDIA_PAUSE -> mOnMediaButtonListener?.onClickPlay()
//                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> mOnMediaButtonListener?.onClickPrevious()
//                        KeyEvent.KEYCODE_MEDIA_NEXT -> mOnMediaButtonListener?.onClickNext()
//                    }
//                }
//                return true
//            }

            override fun onSkipToPrevious() {
                mOnMediaButtonListener?.onClickPrevious()
            }

            override fun onPlay() {
                mOnMediaButtonListener?.onClickPlay()
            }

            override fun onPause() {
                mOnMediaButtonListener?.onClickPlay()
            }

            override fun onSkipToNext() {
                mOnMediaButtonListener?.onClickNext()
            }

            override fun onSeekTo(pos: Long) {
                mOnMediaButtonListener?.onSeekTo(pos)
            }
        })
        mediaSession.isActive = true
    }

    fun getSessionToken(): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }

    fun updatePlayState(isPlaying: Boolean?, position: Long?, playSpeed: Float?) {

        val playingState =
            if (isPlaying == null) mediaSession.controller?.playbackState?.state else {
                if (isPlaying == true) PlaybackStateCompat.STATE_PLAYING else {
                    PlaybackStateCompat.STATE_PAUSED
                }
            }

        val playbackPosition = position ?: mediaSession.controller?.playbackState?.position
        val playbackSpeed = playSpeed ?: 1.0f
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(
                    playingState ?: PlaybackStateCompat.STATE_PAUSED,
                    playbackPosition ?: 0,
                    playbackSpeed
                )
                .build()
        )
    }

    fun updatePlayInfo(
        duration: Long?,
        title: String?,
        subTitle: String?,
        resource: Bitmap? = null
    ) {
        val metaDataBuilder = MediaMetadataCompat.Builder()
        if (duration != null && duration > 0) {
            metaDataBuilder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                duration
            )
        }

        if (title != null) {
            metaDataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        }

        if (subTitle != null) {
            metaDataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subTitle)
        }

        if (resource != null) {
            metaDataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, resource)
        }

        mediaSession.setMetadata(metaDataBuilder.build())

    }

    fun setOnMediaButtonListener(listener: OnMediaButtonListener) {
        mOnMediaButtonListener = listener;
    }
}

public interface OnMediaButtonListener {
    fun onClickPlay()
    fun onClickPrevious()
    fun onClickNext()
    fun onSeekTo(position: Long)
}