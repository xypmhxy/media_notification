package com.nl.media.notification.config

import com.nl.media.notification.DefaultConstant

class NotificationConfigManager private constructor() {

    private var mConfig: NotificationConfig = NotificationConfig(
        appIcon = DefaultConstant.APP_ICON,
        androidLargeIcon = DefaultConstant.LARGE_ICON,
        androidSmallIcon = DefaultConstant.SMALL_ICON,
//        androidPlayIcon = DefaultConstant.PLAY_ICON,
//        androidPauseIcon = DefaultConstant.PAUSE_ICON,
//        androidPreIcon = DefaultConstant.PRE_ICON,
//        androidNextIcon = DefaultConstant.NEXT_ICON,
        imageWidth = 300.0,
        imageHeight = 300.0,
    )

    companion object {
        val get by lazy(LazyThreadSafetyMode.NONE) {
            NotificationConfigManager()
        }
    }

    fun setConfig(config: NotificationConfig) {
        mConfig = config;
    }

    fun getConfig(): NotificationConfig {
        return mConfig
    }
}