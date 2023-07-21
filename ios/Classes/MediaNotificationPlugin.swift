import Flutter
import UIKit

public class MediaNotificationPlugin: NSObject, FlutterPlugin, CommandCenterDelegate {
    
    let notificationController = NotificationController()
    static var mChannel: FlutterMethodChannel!
    
    public override init() {
        super.init()
        notificationController.setupCommand(delegate: self)
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_media_notification_plugin", binaryMessenger: registrar.messenger())
        let instance = MediaNotificationPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
        mChannel = channel
    }
    
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        
        if call.method == "updateNotification"{
            guard call.arguments is Dictionary<String,Any> else {
                result(false)
                return
            }
            
            let notificationInfo = MediaNotificationInfo.createFromDictionary(dictionary: call.arguments as! Dictionary<String, Any>)
            notificationController.updateNotification(notificationInfo: notificationInfo)
            result(true)
        }else if call.method == "updateConfig"{
            guard call.arguments is Dictionary<String,Any> else {
                result(false)
                return
            }
            let dictionary = call.arguments as! Dictionary<String,Any>
            let notificationConfig = MediaNotificationConfig()
            notificationConfig.imageWidth = dictionary["imageWidth"] as? Double
            notificationConfig.imageHeight = dictionary["imageWidth"] as? Double
            NotificationConfigManager.shared.setConfig(config: notificationConfig)
            result(true)
        }else if call.method == "updatePlayState"{
            guard call.arguments is Dictionary<String,Any> else {
                result(false)
                return
            }
            let dictionary = call.arguments as! Dictionary<String,Any>
            let isPlaying = (dictionary["isPlaying"] as? Bool) ?? false
            let position = (dictionary["position"] as? Int) ?? 0
            notificationController.updatePlayState(isPlaying: isPlaying,position: position)
        }
    }
    
    func onClickPlay() {
        MediaNotificationPlugin.mChannel.invokeMethod("play", arguments: [])
    }
    
    func onClickPrevious() {
        MediaNotificationPlugin.mChannel.invokeMethod("previous", arguments: [])
    }
    
    func onClickNext() {
        MediaNotificationPlugin.mChannel.invokeMethod("next", arguments: [])
    }
    
    func onSeekTo(position: Int) {
        MediaNotificationPlugin.mChannel.invokeMethod("seekTo", arguments: position)
    }
}
