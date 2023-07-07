package com.nl.media.notification.media_notification

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.NonNull
import com.nl.media.notification.LifeCycleManager
import com.nl.media.notification.MediaServer
import com.nl.media.notification.MediaServerBinder
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
    private var mConnection: ServiceConnection? = null
    private var mNotificationReceiver: NotificationControlBroadcast? = null
    private var mService: MediaServer? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mContext = flutterPluginBinding.applicationContext

        channel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_media_notification_plugin")
        channel.setMethodCallHandler(this)

        MediaSessionUser.get.initMediaSession(mContext!!)
        MediaSessionUser.get.setOnMediaButtonListener(this)

//        registerNotificationReceiver()
        ScreenUtil.init(mContext!!)
        LifeCycleManager.get.register()
        bindServer(mContext!!)

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
                NotificationUIManager.get.updateNotification(mService!!, mediaNotificationInfo)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        unRegisterNotificationReceiver()
        if (mConnection != null)
            binding.applicationContext.unbindService(mConnection!!)
    }

    /**
     * 启动服务
     */

    private fun bindServer(context: Context) {
        mConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                Log.i("rqrq","onServiceConnected")
                val serverBinder = binder as? MediaServerBinder ?: return
                mService = serverBinder.service
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                mService = null
            }
        }
        context.bindService(
            Intent(context, MediaServer::class.java),
            mConnection!!,
            Service.BIND_AUTO_CREATE
        )
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
