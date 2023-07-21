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
        config.imageWidth = 300.0
        config.imageHeight = 300.0
    }
    
    func setConfig(config: MediaNotificationConfig){
        self.config = config
    }
    
    func getConfig() -> MediaNotificationConfig{
        return config
    }
}
