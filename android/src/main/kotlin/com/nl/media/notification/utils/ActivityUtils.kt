package com.nl.media.notification.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo

class ActivityUtils {

    companion object {
        private var mainTargetClassName: String? = null

        fun getMainTargetClass(context: Context): Class<*> {
            val packageName: String = context.packageName
            val intent = Intent()
            intent.setPackage(packageName)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val resolveInfoList: List<ResolveInfo> = context
                .packageManager
                .queryIntentActivities(intent, 0)

            if (resolveInfoList.isNotEmpty()) {
                mainTargetClassName = resolveInfoList.first().activityInfo.name
            }

            if (mainTargetClassName == null) {
                mainTargetClassName = "$packageName.MainActivity"
            }

            val clazz = tryResolveClassName(mainTargetClassName!!)
            if (clazz != null) {
                return clazz
            }

            return tryResolveClassName("MainActivity")!!
        }

        private fun tryResolveClassName(className: String): Class<*>? {
            return try {
                Class.forName(className)
            } catch (e: ClassNotFoundException) {
                return null
            }
        }
    }

}