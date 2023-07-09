package com.nl.media.notification.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import io.flutter.view.FlutterMain
import java.io.File
import java.io.InputStream
import java.util.regex.Pattern

class BitmapUtils {
    companion object {
        fun getBitmapFromAssets(context: Context, assets: String): Bitmap? {
            val bitmapPath = cleanMediaPath(assets) ?: return null
            var bitmap: Bitmap? = null
            var inputStream: InputStream? = null
            try {
                inputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.assets.open("flutter_assets/assets/$bitmapPath")
                } else {
                    val assetLookupKey = FlutterMain.getLookupKeyForAsset(bitmapPath)
                    val assetManager = context.assets
                    val assetFileDescriptor = assetManager.openFd(assetLookupKey)
                    assetFileDescriptor.createInputStream()
                }
                bitmap = BitmapFactory.decodeStream(inputStream)
                return bitmap
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun getDrawableResourceId(context: Context, bitmapReference: String): Int {
            val cleanBitmapReference = cleanMediaPath(bitmapReference) ?: return 0
            val reference = cleanBitmapReference.split("/").toTypedArray()
            try {
                var resId: Int
                val type = reference[0]
                val label = reference[1]

                // Resources protected from obfuscation
                // https://developer.android.com/studio/build/shrink-code#strict-reference-checks
                val name = String.format("res_%1s", label)
                resId = context.resources.getIdentifier(name, type, context.packageName)
                if (resId == 0) {
                    resId = context.resources.getIdentifier(label, type, context.packageName)
                }
                return resId
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }
            return 0
        }

        fun getBitmapFromFile(imagePath: String): Bitmap? {
            val bitmapPath = cleanMediaPath(imagePath) ?: return null
            val imageFile = File(bitmapPath)
            return BitmapFactory.decodeFile(imageFile.absolutePath)
        }

        private fun cleanMediaPath(mediaPath: String?): String? {
            if (mediaPath != null) {
                val pattern = Pattern.compile("^https?:\\/\\/", Pattern.CASE_INSENSITIVE)
                val pattern2 = Pattern.compile("^(assets:\\/\\/)(.*)", Pattern.CASE_INSENSITIVE)
                val pattern3 = Pattern.compile("^(file:\\/\\/)(.*)", Pattern.CASE_INSENSITIVE)
                val pattern4 = Pattern.compile("^(resource:\\/\\/)(.*)", Pattern.CASE_INSENSITIVE)
                if (pattern.matcher(mediaPath).find()) {
                    return mediaPath
                }
                if (pattern2.matcher(mediaPath).find()) {
                    return pattern2.matcher(mediaPath).replaceAll("$2")
                }
                if (pattern3.matcher(mediaPath).find()) {
                    return pattern3.matcher(mediaPath).replaceAll("/$2")
                }
                if (pattern4.matcher(mediaPath).find()) {
                    return pattern4.matcher(mediaPath).replaceAll("$2")
                }
            }
            return null
        }
    }
}