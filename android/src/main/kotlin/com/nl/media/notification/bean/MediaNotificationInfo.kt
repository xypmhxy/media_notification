package com.nl.media.notification.bean

import com.nl.media.notification.config.NotificationConfig

class MediaNotificationInfo(
    var title: String? = null,
    var subtitle: String? = null,
    var hasNext: Boolean? = null,
    var hasPre: Boolean? = null,
    var isPlaying: Boolean? = null,
    var duration: Long? = null,
    var position: Long? = null,
    var playSpeed: Float? = null,
    var placeHolderAssets: String? = null,
    var imagePath: String? = null,
    var imageData: ByteArray? = null,
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): MediaNotificationInfo {
            return MediaNotificationInfo(
                title = map["title"] as? String?,
                subtitle = map["subtitle"] as? String?,
                hasNext = map["hasNext"] as? Boolean?,
                hasPre = map["hasPre"] as? Boolean?,
                isPlaying = map["isPlaying"] as? Boolean?,
                duration = (map["duration"] as? Int?)?.toLong(),
                position = (map["position"] as? Int?)?.toLong(),
                playSpeed = map["playSpeed"] as? Float?,
                placeHolderAssets = map["placeHolderAssets"] as? String?,
                imagePath = map["imagePath"] as? String?,
                imageData = map["imageData"] as? ByteArray?,
            )
        }
    }
}