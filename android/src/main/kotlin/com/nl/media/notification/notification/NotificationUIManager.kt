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

class NotificationUIManager private constructor() {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var mNotification: Notification? = null
    var isAllowReLoadNotification = true

    companion object {

        const val CHANNEL_ID = "MediaNotificationChannelId"
        const val MEDIA_FOREGROUND_ID = 120

        val get by lazy(LazyThreadSafetyMode.NONE) {
            NotificationUIManager()
        }
    }

    fun updateNotification(context: Context, notificationInfo: MediaNotificationInfo) {
        updateInfo(context, notificationInfo)
        updateState(notificationInfo)
    }

     fun updateState(notificationInfo: MediaNotificationInfo) {
        MediaSessionUser.get.updatePlayState(
            isPlaying = notificationInfo.isPlaying,
            position = notificationInfo.position,
            playSpeed = notificationInfo.playSpeed
        )
    }

    private fun updateInfo(context: Context, notificationInfo: MediaNotificationInfo) {
        MediaSessionUser.get.updatePlayInfo(
            duration = notificationInfo.duration,
            title = notificationInfo.title,
            subTitle = notificationInfo.subtitle
        )

        val imageData = notificationInfo.imageData ?: byteArrayOf()
        val imagePath = notificationInfo.imagePath ?: ""
        loadImage(
            context,
            imageData = imageData,
            imagePath = imagePath,
            placeholderRes = 0,
            onLoadSuccess = { resource ->
                MediaSessionUser.get.updatePlayInfo(
                    duration = notificationInfo.duration,
                    title = notificationInfo.title,
                    subTitle = notificationInfo.subtitle,
                    resource = resource
                )
            })
    }

    /**
     * 如果未展示通知栏或者设置更改则刷新否则不刷新
     */
    fun tryShowNotification(context: Context) {
        if (!isAllowReLoadNotification) {
            mNotificationManager?.notify(MEDIA_FOREGROUND_ID, mNotification)
            return
        }
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

        mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setLargeIcon(largeIconBitmap)
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
        isAllowReLoadNotification = false;
    }

    private fun loadImage(
        context: Context,
        imageData: ByteArray,
        imagePath: String,
        placeholderRes: Int,
        onLoadSuccess: (resource: Bitmap) -> Unit
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
                onLoadSuccess(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

}