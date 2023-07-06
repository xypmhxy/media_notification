package com.nl.media.notification.media_notification

import android.content.Context
import android.content.IntentFilter
import androidx.annotation.NonNull
import com.nl.media.notification.LifeCycleManager
import com.nl.media.notification.bean.MediaNotificationInfo
import com.nl.media.notification.broadcast.NotificationControlBroadcast
import com.nl.media.notification.config.NotificationConfig
import com.nl.media.notification.config.NotificationConfigManager
import com.nl.media.notification.mediaSession.MediaSessionUser
import com.nl.media.notification.mediaSession.OnMediaButtonListener
import com.nl.media.notification.notification.NotificationUIManager
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
    private var mNotificationReceiver: NotificationControlBroadcast? = null;

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mContext = flutterPluginBinding.applicationContext

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_media_notification_plugin")
        channel.setMethodCallHandler(this)

        MediaSessionUser.get.initMediaSession(mContext!!)
        MediaSessionUser.get.setOnMediaButtonListener(this)

//        registerNotificationReceiver()
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
                result.success(true)
            }

            "updateNotification" -> {
                if (call.arguments !is Map<*, *>) {
                    result.success(false)
                    return
                }
                val mediaNotificationInfo =
                    MediaNotificationInfo.fromMap(call.arguments as Map<String, Any>)
                NotificationUIManager.get.updateNotification(mContext!!, mediaNotificationInfo)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        unRegisterNotificationReceiver()
    }

    /**
     * 注册通知栏点击回掉
     */
    private fun registerNotificationReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationControlBroadcast.ACTION_NOTIFY)
        intentFilter.addAction(NotificationControlBroadcast.ACTION_PREV)
        intentFilter.addAction(NotificationControlBroadcast.ACTION_NEXT)
        intentFilter.addAction(NotificationControlBroadcast.ACTION_CLICK_PLAY)
        mNotificationReceiver = NotificationControlBroadcast()
        mContext?.registerReceiver(mNotificationReceiver, intentFilter)
    }

    private fun unRegisterNotificationReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationControlBroadcast.ACTION_NOTIFY)
        intentFilter.addAction(NotificationControlBroadcast.ACTION_PREV)
        intentFilter.addAction(NotificationControlBroadcast.ACTION_NEXT)
        intentFilter.addAction(NotificationControlBroadcast.ACTION_CLICK_PLAY)
        mContext?.unregisterReceiver(mNotificationReceiver)
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
}
