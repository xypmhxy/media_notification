package com.nl.media.notification.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.nl.media.notification.MediaServer
import com.nl.media.notification.bean.MediaNotificationInfo
import com.nl.media.notification.broadcast.NotificationControlBroadcast.Companion.ACTION_CLICK_PLAY
import com.nl.media.notification.broadcast.NotificationControlBroadcast.Companion.ACTION_NEXT
import com.nl.media.notification.broadcast.NotificationControlBroadcast.Companion.ACTION_PREV
import com.nl.media.notification.config.NotificationConfigManager
import com.nl.media.notification.mediaSession.MediaSessionUser
import com.nl.media.notification.utils.ActivityUtils
import com.nl.media.notification.utils.BitmapUtils
import com.nl.media.notification.utils.ScreenUtil

class NotificationUIManager private constructor() {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var mNotification: Notification? = null
    private var largeIconBitmap: Bitmap? = null

    companion object {

        const val CHANNEL_ID = "MediaNotificationChannelId"
        const val MEDIA_FOREGROUND_ID = 124

        val get by lazy(LazyThreadSafetyMode.NONE) {
            NotificationUIManager()
        }
    }

    fun updateNotification(context: MediaServer, notificationInfo: MediaNotificationInfo) {
        updateState(notificationInfo)
        updateNotificationInfo(context, notificationInfo)
    }

    private fun updateState(notificationInfo: MediaNotificationInfo) {
        MediaSessionUser.get.updatePlayState(
            isPlaying = notificationInfo.isPlaying,
            position = notificationInfo.position,
            duration = notificationInfo.duration,
            playSpeed = notificationInfo.playSpeed
        )
    }

    private fun updateNotificationInfo(context: MediaServer, notificationInfo: MediaNotificationInfo) {
        val notificationConfig = NotificationConfigManager.get.getConfig()

        if (mNotificationManager == null) {
            mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager?
        }
        val pendingLast = PendingIntent.getBroadcast(
            context,
            1,
            Intent(ACTION_PREV),
            PendingIntent.FLAG_IMMUTABLE
        )
        val pendingNext = PendingIntent.getBroadcast(
            context,
            2,
            Intent(ACTION_NEXT),
            PendingIntent.FLAG_IMMUTABLE
        )
        val pendingPlay = PendingIntent.getBroadcast(
            context,
            3,
            Intent(ACTION_CLICK_PLAY),
            PendingIntent.FLAG_IMMUTABLE
        )
        val intent = Intent(context, ActivityUtils.getMainTargetClass(context))
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingTurn =
            PendingIntent.getActivity(context, 4, intent, PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                javaClass.simpleName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setShowBadge(false)
            if (mNotificationManager != null) {
                mNotificationManager!!.createNotificationChannel(notificationChannel)
            }
        }

        val title = notificationInfo.title ?: "- -"
        val subtitle = notificationInfo.subtitle ?: "- -"
        val isPlaying = notificationInfo.isPlaying ?: false
        val imageData = notificationInfo.imageData ?: byteArrayOf()
        val imagePath = notificationInfo.imagePath ?: ""

        val preResId: Int =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidPreIcon ?: "")
        val nextResId: Int =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidNextIcon ?: "")

        val playIcon =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidPlayIcon ?: "")
        val pauseIcon =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidPauseIcon ?: "")
        val playPauseResId: Int = if (isPlaying) pauseIcon else playIcon

        var largeIconRes =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidLargeIcon ?: "")
        if (largeIconRes == 0) {
            largeIconRes =
                BitmapUtils.getDrawableResourceId(context, notificationConfig.appIcon ?: "")
        }
        val largeIconBitmap = BitmapFactory.decodeResource(context.resources, largeIconRes)

        var smallIcon =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidSmallIcon ?: "")
        if (smallIcon == 0) {
            smallIcon = BitmapUtils.getDrawableResourceId(context, notificationConfig.appIcon ?: "")
        }

        loadImage(context,imageData = imageData,imagePath=imagePath, placeholderRes = 0)

        mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setLargeIcon(this.largeIconBitmap?:largeIconBitmap)
            .setContentIntent(pendingTurn)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(smallIcon)
            .addAction(preResId, "Pre", pendingLast)
            .addAction(playPauseResId, "Play", pendingPlay)
            .addAction(nextResId, "Next", pendingNext)
            .setOngoing(true)
            .setShowWhen(false)
            .setSilent(true)
            .setLights(NotificationCompat.DEFAULT_LIGHTS, 3000, 3000)
            .setPriority(Notification.PRIORITY_MAX)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(MediaSessionUser.get.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2)
            )
        mNotification = mBuilder!!.build()
//        mNotification?.flags = NotificationCompat.FLAG_SHOW_LIGHTS
//        mNotificationManager?.notify(MEDIA_FOREGROUND_ID, mNotification)

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.startForeground(
                MEDIA_FOREGROUND_ID, mNotification!!, ServiceInfo
                    .FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
            true
        } else {
            context.startForeground(MEDIA_FOREGROUND_ID, mNotification!!)
            true
        }
    }

    private fun loadImage(
        context: Context,
        imageData: ByteArray,
        imagePath: String,
        placeholderRes: Int
    ) {
        if (imageData.isEmpty() && imagePath.isEmpty()) {
            return;
        }
        var any: Any = imagePath
        if (imageData.isNotEmpty()) {
            any = imageData;
        } else if (imagePath.startsWith("assets://")) {
            val bitmap = BitmapUtils.getBitmapFromAssets(context, imagePath)
            if (bitmap != null) {
                any = bitmap
            }
        } else if (imagePath.startsWith("file://")) {
            val bitmap = BitmapUtils.getBitmapFromFile(imagePath)
            if (bitmap != null) {
                any = bitmap
            }
        } else if (imagePath.startsWith("http")) {
            any = imagePath;
        }
        var requestBuilder = Glide.with(context).asBitmap().load(any).apply(
            RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(ScreenUtil.dp2px(4f))
            )
        )
        if (placeholderRes != 0) {
            requestBuilder = requestBuilder.error(placeholderRes)
        }
        val notificationConfig = NotificationConfigManager.get.getConfig()
        notificationConfig.imageWidth?.toInt()
            ?.let {
                notificationConfig.imageHeight?.toInt()
                    ?.let { it1 -> requestBuilder.override(it, it1) }
            }
        requestBuilder.into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                if (mBuilder == null) return
                largeIconBitmap = resource
                mBuilder!!.setLargeIcon(resource)
                mNotificationManager?.notify(MEDIA_FOREGROUND_ID, mBuilder!!.build())
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
            }
        })
    }

}