//
//  MediaNotificationInfo.swift
//  Kingfisher
//
//  Created by zjs on 2023/7/21.
//

import Foundation
import Flutter

class MediaNotificationInfo{
    var title: String?
    var subtitle: String?
    var hasNext: Bool?
    var hasPre: Bool?
    var isPlaying: Bool?
    var duration: Int?
    var position: Int?
    var playSpeed: Float?
    var imagePath: String?
    var imageData: FlutterStandardTypedData?
    
    static func createFromDictionary(dictionary: Dictionary<String,Any>) -> MediaNotificationInfo{
        let notificationInfo = MediaNotificationInfo()
        notificationInfo.title = dictionary["title"] as? String
        notificationInfo.subtitle = dictionary["subtitle"] as? String
        notificationInfo.hasNext = dictionary["hasNext"] as? Bool
        notificationInfo.hasPre = dictionary["hasPre"] as? Bool
        notificationInfo.isPlaying = dictionary["isPlaying"] as? Bool
        notificationInfo.duration = dictionary["duration"] as? Int
        notificationInfo.position = dictionary["position"] as? Int
        notificationInfo.playSpeed = dictionary["playSpeed"] as? Float
        notificationInfo.imagePath = dictionary["imagePath"] as? String
        notificationInfo.imageData = dictionary["imageData"] as? FlutterStandardTypedData
        return notificationInfo
    }
}
