//
//  NotificationConfigManager.swift
//  media_notification
//
//  Created by zjs on 2023/7/21.
//

import Foundation

class NotificationConfigManager {
    static let shared = NotificationConfigManager()
    private var config = MediaNotificationConfig()
    
    public init() {
        config.imageWidth = 196.0
        config.imageHeight = 196.0
    }
    
    func setConfig(config: MediaNotificationConfig){
        self.config = config
    }
    
    func getConfig() -> MediaNotificationConfig{
        return config
    }
}
