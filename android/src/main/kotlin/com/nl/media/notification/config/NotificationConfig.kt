package com.nl.media.notification.config

class NotificationConfig(
    var appIcon: String? = null,
    var androidLargeIcon: String? = null,
    var androidSmallIcon: String? = null,
//    var androidPlayIcon: String? = null,
//    var androidPauseIcon: String? = null,
//    var androidPreIcon: String? = null,
//    var androidNextIcon: String? = null,
    var androidPlaceholderImage: String? = null,
    var imageWidth: Double? = null,
    var imageHeight: Double? = null,
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): NotificationConfig {
            return NotificationConfig(
                appIcon = map["appIcon"] as? String?,
                androidLargeIcon = map["androidLargeIcon"] as? String?,
                androidSmallIcon = map["androidSmallIcon"] as? String?,
//                androidPlayIcon = map["androidPlayIcon"] as? String?,
//                androidPauseIcon = map["androidPauseIcon"] as? String?,
//                androidPreIcon = map["androidPreIcon"] as? String?,
//                androidNextIcon = map["androidNextIcon"] as? String?,
                androidPlaceholderImage = map["androidPlaceholderImage"] as? String?,
                imageWidth = map["imageWidth"] as? Double?,
                imageHeight = map["imageHeight"] as? Double?,
            )
        }
    }
}