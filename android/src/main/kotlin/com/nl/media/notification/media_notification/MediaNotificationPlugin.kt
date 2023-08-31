package com.nl.media.notification.media_notification

import android.content.Context
import android.content.IntentFilter
import androidx.annotation.NonNull
import com.nl.media.notification.LifeCycleManager
import com.nl.media.notification.bean.MediaNotificationInfo
import com.nl.media.notification.config.NotificationConfig
import com.nl.media.notification.config.NotificationConfigManager
import com.nl.media.notification.mediaSession.MediaSessionUser
import com.nl.media.notification.mediaSession.OnMediaButtonListener
import com.nl.media.notification.notification.NotificationAndroid12Shower
import com.nl.media.notification.notification.NotificationCommandReceiver
import com.nl.media.notification.notification.NotificationUIManager
import com.nl.media.notification.utils.AppUtils
import com.nl.media.notification.utils.ScreenUtil

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** MediaNotificationPlugin */
class MediaNotificationPlugin : FlutterPlugin, MethodCallHandler, OnMediaButtonListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var mContext: Context? = null
    private val mNotificationCommandReceiver = NotificationCommandReceiver()

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mContext = flutterPluginBinding.applicationContext

        channel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_media_notification_plugin")
        channel.setMethodCallHandler(this)

        MediaSessionUser.get.initMediaSession(mContext!!)
        MediaSessionUser.get.setOnMediaButtonListener(this)

        registerNotificationReceiver(mContext!!)
        ScreenUtil.init(mContext!!)
        LifeCycleManager.get.register()
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (mContext == null) {
            result.success(false)
            return
        }
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            "updateConfig" -> {
                if (call.arguments !is Map<*, *>) {
                    result.success(false)
                    return
                }
                NotificationConfigManager.get.setConfig(NotificationConfig.fromMap(call.arguments as Map<String, Any>))
                NotificationUIManager.get.isAllowReLoadNotification = true
                result.success(true)
            }

            "updateNotification" -> {
                if (call.arguments !is Map<*, *>) {
                    result.success(false)
                    return
                }
                val mediaNotificationInfo =
                    MediaNotificationInfo.fromMap(call.arguments as Map<String, Any>)
                updateNotification(mContext!!, mediaNotificationInfo)
//                NotificationUIManager.get.updateNotification(mContext!!, mediaNotificationInfo)
//                NotificationUIManager.get.tryShowNotification(
//                    mContext!!, isPlaying = mediaNotificationInfo.isPlaying
//                        ?: false
//                )
                result.success(true)
            }

            "updatePlayState" -> {
                if (call.arguments !is Map<*, *>) {
                    result.success(false)
                    return
                }
                val params = call.arguments as Map<String, Any>
                val isPlaying = params["isPlaying"] as? Boolean
                updateNotification(mContext!!, MediaNotificationInfo(isPlaying = isPlaying))
//                NotificationUIManager.get.updateState(MediaNotificationInfo(isPlaying = isPlaying))
//                NotificationUIManager.get.tryShowNotification(
//                    mContext!!, isPlaying = isPlaying
//                        ?: false
//                )
                result.success(true)
            }

            "updatePosition" -> {
                val position = call.arguments as? Int
                NotificationUIManager.get.updateState(MediaNotificationInfo(position = position?.toLong()))
                result.success(true)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun registerNotificationReceiver(context: Context) {
        val intentFilter = IntentFilter(NotificationCommandReceiver.ACTION_PREVIOUS)
        intentFilter.addAction(NotificationCommandReceiver.ACTION_PLAY_PAUSE)
        intentFilter.addAction(NotificationCommandReceiver.ACTION_NEXT)
        context.registerReceiver(mNotificationCommandReceiver, intentFilter)
        mNotificationCommandReceiver.setOnMediaButtonListener(this)
    }

    private fun updateNotification(context: Context, notificationInfo: MediaNotificationInfo) {
        if (AppUtils.isAndroid33())
            NotificationUIManager.get.updateNotification(context, notificationInfo)
        else
            NotificationAndroid12Shower.get.updateNotification(context, notificationInfo)
    }

    /**
     * 多媒体按钮回调/通知栏按钮回调
     */
    override fun onClickPlay() {
        channel.invokeMethod("play", emptyMap<String, String>())
    }

    override fun onClickPrevious() {
        channel.invokeMethod("previous", emptyMap<String, String>())
    }

    override fun onClickNext() {
        channel.invokeMethod("next", emptyMap<String, String>())
    }

    override fun onSeekTo(position: Long) {
        channel.invokeMethod("seekTo", position)
    }
}
