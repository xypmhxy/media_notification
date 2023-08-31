package com.nl.media.notification.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.nl.media.notification.bean.MediaNotificationInfo
import com.nl.media.notification.config.NotificationConfigManager
import com.nl.media.notification.mediaSession.MediaSessionUser
import com.nl.media.notification.utils.ActivityUtils
import com.nl.media.notification.utils.BitmapUtils
import com.nl.media.notification.utils.ScreenUtil

class NotificationAndroid12Shower {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var mNotification: Notification? = null
    private var mLargeIconBitmap: Bitmap? = null

    companion object {

        const val CHANNEL_ID = "MediaNotificationChannelId"
        const val MEDIA_FOREGROUND_ID = 120

        val get by lazy(LazyThreadSafetyMode.NONE) {
            NotificationAndroid12Shower()
        }
    }

    fun updateNotification(context: Context, notificationInfo: MediaNotificationInfo) {
        tryShowNotification(context, notificationInfo)
        MediaSessionUser.get.updatePlayInfo(
            duration = notificationInfo.duration,
            title = notificationInfo.title,
            subTitle = notificationInfo.subtitle
        )
        MediaSessionUser.get.updatePlayState(
            isPlaying = notificationInfo.isPlaying,
            position = notificationInfo.position,
            playSpeed = notificationInfo.playSpeed
        )
        val imageData = notificationInfo.imageData ?: byteArrayOf()
        val imagePath = notificationInfo.imagePath ?: ""
        val placeHolderAssets = notificationInfo.placeHolderAssets ?: ""
        loadImage(
            context,
            imageData = imageData,
            imagePath = imagePath,
            placeholderAssets = placeHolderAssets
        )
    }

    private fun tryShowNotification(context: Context,  notificationInfo: MediaNotificationInfo) {
        val notificationConfig = NotificationConfigManager.get.getConfig()
        if (mNotificationManager == null) {
            mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        }

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
        if (mLargeIconBitmap == null) {
            var largeIconRes =
                BitmapUtils.getDrawableResourceId(context, notificationConfig.androidLargeIcon ?: "")
            if (largeIconRes == 0) {
                largeIconRes =
                    BitmapUtils.getDrawableResourceId(context, notificationConfig.appIcon ?: "")
            }
            mLargeIconBitmap = BitmapFactory.decodeResource(context.resources, largeIconRes)
        }

        var smallIcon =
            BitmapUtils.getDrawableResourceId(context, notificationConfig.androidSmallIcon ?: "")
        if (smallIcon == 0) {
            smallIcon = BitmapUtils.getDrawableResourceId(context, notificationConfig.appIcon ?: "")
        }

        val pendingPrevious = PendingIntent.getBroadcast(
            context,
            1,
            Intent(NotificationCommandReceiver.ACTION_PREVIOUS),
            PendingIntent.FLAG_IMMUTABLE
        )
        val pendingPlay = PendingIntent.getBroadcast(
            context,
            3,
            Intent(NotificationCommandReceiver.ACTION_PLAY_PAUSE),
            PendingIntent.FLAG_IMMUTABLE
        )
        val pendingNext = PendingIntent.getBroadcast(
            context,
            2,
            Intent(NotificationCommandReceiver.ACTION_NEXT),
            PendingIntent.FLAG_IMMUTABLE
        )

        val isPlaying = notificationInfo.isPlaying ?: false
        val res = if (isPlaying) notificationConfig.androidPauseIcon!! else notificationConfig.androidPlayIcon!!

        mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .addAction(
                BitmapUtils.getDrawableResourceId(context, notificationConfig.androidPreIcon!!),
                "",
                pendingPrevious
            )
            .addAction(
                BitmapUtils.getDrawableResourceId(context, res),
                "",
                pendingPlay
            )
            .addAction(
                BitmapUtils.getDrawableResourceId(context, notificationConfig.androidNextIcon!!),
                "",
                pendingNext
            )
            .setLargeIcon(mLargeIconBitmap)
            .setContentIntent(pendingTurn)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(smallIcon)
            .setOngoing(true)
            .setShowWhen(false)
            .setSilent(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(MediaSessionUser.get.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2)
            )
        mNotification = mBuilder!!.build()
        mNotification?.flags =
            NotificationCompat.FLAG_FOREGROUND_SERVICE or NotificationCompat.FLAG_NO_CLEAR
        mNotificationManager?.notify(MEDIA_FOREGROUND_ID, mNotification)
    }

    private fun loadImage(
        context: Context,
        imageData: ByteArray,
        imagePath: String,
        placeholderAssets: String
    ) {
        var any: Any = imagePath
        if (imageData.isEmpty() && imagePath.isEmpty()) {
            val placeHolderBitmap = BitmapUtils.getBitmapFromAssets(context, placeholderAssets) ?: return
            any = placeHolderBitmap
        }
        if (imageData.isNotEmpty()) {
            any = imageData;
        } else if (imagePath.startsWith("assets://")) {
            val bitmap = BitmapUtils.getBitmapFromAssets(context, imagePath)
            if (bitmap != null) {
                any = bitmap;
            }
        } else if (imagePath.startsWith("file://")) {
            val bitmap = BitmapUtils.getBitmapFromFile(imagePath)
            if (bitmap != null) {
                any = bitmap;
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
        if (placeholderAssets != "") {
            requestBuilder = requestBuilder.error(placeholderAssets)
        }
        val notificationConfig = NotificationConfigManager.get.getConfig()
        notificationConfig.imageWidth?.toInt()
            ?.let {
                notificationConfig.imageHeight?.toInt()
                    ?.let { it1 -> requestBuilder.override(it, it1) }
            }
        requestBuilder.into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                if (mBuilder != null) {
                    mLargeIconBitmap = resource
                    mBuilder?.setLargeIcon(mLargeIconBitmap)
                    mNotificationManager?.notify(MEDIA_FOREGROUND_ID, mBuilder!!.build())
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }
}