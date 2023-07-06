package com.nl.media.notification.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.*
import androidx.core.view.ViewCompat
import java.lang.reflect.Method

class ScreenUtil {
   companion object{
       var sDensity = 0f
       var sDensityDpi = 0
       var sFontDensity = 0f

       //屏幕宽度
       var sWidthPixels = 0

       //屏幕高度
       var sHeightPixels = 0
       var navBarHeightPixels = 0

       //状态栏高度
       var statusBarHeight = 0

       //刘海屏高度
       var safeInsetTop = 0

       fun init(context: Context?) {
           if (context != null && context.resources != null) {
               val displayMetrics = context.resources.displayMetrics
               sDensity = displayMetrics.density
               sFontDensity = displayMetrics.scaledDensity
               sWidthPixels = displayMetrics.widthPixels
               sHeightPixels = displayMetrics.heightPixels

//            statusBarHeight = getStatusBarHeight(context);
               sDensityDpi = displayMetrics.densityDpi
           }
       }

       /**
        * 是否有虛擬按鍵
        */
       fun hasNavBar(paramContext: Context): Boolean {
           var bool = true
           val sNavBarOverride: String
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               try {
                   var localObject: Any = Class.forName("android.os.SystemProperties").getDeclaredMethod(
                       "get",
                       String::class.java
                   )
                   (localObject as Method).isAccessible = true
                   sNavBarOverride = localObject.invoke(null, "qemu.hw.mainkeys") as String
                   localObject = paramContext.resources
                   val i = localObject.getIdentifier("config_showNavigationBar", "bool", "android")
                   if (i != 0) {
                       bool = localObject.getBoolean(i)
                       if ("1" == sNavBarOverride) {
                           return false
                       }
                   }
               } catch (ignored: Throwable) {
               }
           }
           return if (!ViewConfiguration.get(paramContext).hasPermanentMenuKey()) {
               bool
           } else false
       }

       /**
        * 获取虚拟按键高度
        */
       private fun getNavigationBarHeight(context: Context?): Int {
           if (null == context) {
               return 0
           }
           val localResources = context.resources
           if (!hasNavBar(context)) {
               return 0
           }
           var i = localResources.getIdentifier("navigation_bar_height", "dimen", "android")
           if (i > 0) {
               return localResources.getDimensionPixelSize(i)
           }
           i = localResources.getIdentifier("navigation_bar_height_landscape", "dimen", "android")
           return if (i > 0) {
               localResources.getDimensionPixelSize(i)
           } else 0
       }

       fun getStatusBarHeight(context: Context): Int {
           if (statusBarHeight == 0) {
               try {
                   val c = Class.forName("com.android.internal.R\$dimen")
                   val o = c.newInstance()
                   val field = c.getField("status_bar_height")
                   val x = field[o] as Int
                   statusBarHeight = context.resources.getDimensionPixelSize(x)
               } catch (e: Exception) {
                   e.printStackTrace()
               }
           }
           return statusBarHeight
       }

       fun sp2px(value: Float): Int {
           return (sFontDensity * value).toInt()
       }

       fun px2sp(value: Float): Int {
           return (value / sFontDensity).toInt()
       }

       fun dp2px(value: Float): Int {
           return (sDensity * value + 0.5f).toInt()
       }

       fun px2dp(value: Float): Int {
           return (value / sDensity + 0.5f).toInt()
       }

       /**
        * 添加高度适配刘海屏
        *
        * @param view 适配目标
        */
       fun addDisplayCutout(view: View) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
               val windowInsets = view.rootView.rootWindowInsets
               if (windowInsets == null) {
                   view.setOnApplyWindowInsetsListener { v: View, insets: WindowInsets? ->
                       addDisplayCutoutInerl(view)
                       v.onApplyWindowInsets(insets)
                   }
               } else {
                   addDisplayCutoutInerl(view)
               }
           }
       }

       /**
        * 添加高度适配刘海屏，只用于[.addDisplayCutout]方法调用
        *
        * @param view 适配目标
        */
       @TargetApi(Build.VERSION_CODES.P)
       private fun addDisplayCutoutInerl(view: View) {
           try {
               view.setOnApplyWindowInsetsListener(null)
               val displayCutout = view.rootView.rootWindowInsets.displayCutout
               if (displayCutout != null) {
                   safeInsetTop = displayCutout.safeInsetTop
                   view.setPadding(
                       view.paddingLeft, view.paddingTop + safeInsetTop, view.paddingRight, view.paddingBottom
                   )
               }
           } catch (ignored: Throwable) {
           }
       }

       /**
        * 判断androidP以上手机是否有刘海屏
        *
        * @param view
        * @return
        */
       fun hasDisplayCutout(view: View): Boolean {
           var isNotchScreen = false
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
               val windowInsets = view.rootView.rootWindowInsets
               if (windowInsets != null) {
                   val displayCutout = windowInsets.displayCutout
                   if (displayCutout != null) {
                       val rects = displayCutout.boundingRects
                       //通过判断是否存在rects来确定是否刘海屏手机
                       if (rects != null && rects.size > 0) {
                           isNotchScreen = true
                       }
                   }
               }
           }
           return isNotchScreen
       }

       @TargetApi(Build.VERSION_CODES.M)
       fun setDeepStatuBar(window: Window, deep: Boolean) {
           val decor = window.decorView
           var ui = decor.systemUiVisibility
           ui = if (deep) {
               ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //设置状态栏中字体的颜色为黑色
           } else {
               ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //设置状态栏中字体颜色为白色
           }
           decor.systemUiVisibility = ui
       }

       /**
        * 设置全屏
        *
        * @param window window对象
        */
       fun changeToFullScreen(window: Window?) {
           if (window != null) {
               val decorView = window.decorView
               var systemUi = decorView.systemUiVisibility
               systemUi = systemUi or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
               systemUi = systemUi or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                       or View.SYSTEM_UI_FLAG_FULLSCREEN
                       or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                   systemUi = systemUi or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
               }
               decorView.systemUiVisibility = systemUi
               window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
               window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
           }
       }

       /**
        * 设置非全屏
        *
        * @param window window对象
        */
       fun changeToNotFullScreen(window: Window?) {
           if (window != null) {
               val decorView = window.decorView
               var systemUi = decorView.systemUiVisibility
               systemUi = systemUi and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
               systemUi = systemUi and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
               systemUi = systemUi or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                   systemUi = systemUi and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
               }
               decorView.systemUiVisibility = systemUi
               window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
               window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
               decorView.post { ViewCompat.requestApplyInsets(decorView) }
           }
       }
   }
}