//
//  NotificationController.swift
//  Kingfisher
//
//  Created by zjs on 2023/7/21.
//

import Foundation
import MediaPlayer

class NotificationController{
    
    var commandDelegate: CommandCenterDelegate? = nil
    
    func setupCommand(delegate: CommandCenterDelegate?){
        UIApplication.shared.beginReceivingRemoteControlEvents()
        let commandCenter = MPRemoteCommandCenter.shared()
        
        // Add handler for Play Command
        commandCenter.playCommand.addTarget { [unowned self] event in
            commandDelegate?.onClickPlay()
            return .success
        }
        
        // Add handler for Pause Command
        commandCenter.pauseCommand.addTarget { [unowned self] event in
            commandDelegate?.onClickPlay()
            return .success
        }
        
        
        commandCenter.previousTrackCommand.addTarget{ [unowned self] event in
            commandDelegate?.onClickPrevious()
            return .success
        }
        
        commandCenter.nextTrackCommand.addTarget{ [unowned self] event in
            commandDelegate?.onClickNext()
            return .success
        }
        
        commandCenter.togglePlayPauseCommand.isEnabled = true
        commandCenter.togglePlayPauseCommand.addTarget{ [unowned self] event in
            commandDelegate?.onClickPlay()
            return .success
        }
        
        commandCenter.changePlaybackPositionCommand.addTarget{ [unowned self] event in
            let positionEvent = event as! MPChangePlaybackPositionCommandEvent
            let time = CMTime.init(seconds: positionEvent.positionTime, preferredTimescale: 1000)
            commandDelegate?.onSeekTo(position: Int(time.value))
            return .success
        }
        
        commandDelegate = delegate
    }
    
    func updateNotification(notificationInfo: MediaNotificationInfo){
        
        let nowPlayingInfo = MPNowPlayingInfoCenter.default().nowPlayingInfo
        var playingInfo = nowPlayingInfo ?? Dictionary<String,Any>()
        if notificationInfo.title != nil {
            playingInfo[MPMediaItemPropertyTitle] = notificationInfo.title
        }
        
        if notificationInfo.subtitle != nil {
            playingInfo[MPMediaItemPropertyArtist] = notificationInfo.subtitle
        }
        
        if notificationInfo.duration != nil {
            playingInfo[MPMediaItemPropertyPlaybackDuration] = notificationInfo.duration! / 1000
        }
        
        if notificationInfo.position != nil {
            playingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = notificationInfo.position! / 1000
        }
        
        if notificationInfo.playSpeed != nil{
            playingInfo[MPNowPlayingInfoPropertyPlaybackRate] = notificationInfo.playSpeed
        }else{
            let isPlaying = notificationInfo.isPlaying ?? false
            playingInfo[MPNowPlayingInfoPropertyPlaybackRate] = isPlaying ? 1.0:0.0
        }
        
        
        let imageData = notificationInfo.imageData
        let imagePath = notificationInfo.imagePath
        let config = NotificationConfigManager.shared.getConfig()
        let imageWidth = config.imageWidth ?? 300.0
        let imageHeight = config.imageHeight ?? 300.0
        
        if (imageData?.data.isEmpty == false){
            guard let image = UIImage(data:Data(imageData!.data)) else {return}
            let artwork = MPMediaItemArtwork.init(boundsSize: CGSizeMake(imageWidth, imageHeight)) { (size) in
                return image
            }
            playingInfo[MPMediaItemPropertyArtwork] = artwork
        }else if imagePath?.starts(with: "http") ?? false {
            let key = imagePath!.md5
            ImageUtils.downloadImage(url: URL(string: imagePath!)!,name: key, completion: {(image) -> Void in
                DispatchQueue.main.async {
                    let artwork = MPMediaItemArtwork.init(boundsSize: CGSizeMake(imageWidth, imageHeight)) { (size) in
                        return image
                    }
                    playingInfo[MPMediaItemPropertyArtwork] = artwork
                    MPNowPlayingInfoCenter.default().nowPlayingInfo = playingInfo
                }
            })
        }
        
        MPNowPlayingInfoCenter.default().nowPlayingInfo = playingInfo
        
        let commandCenter = MPRemoteCommandCenter.shared()
        commandCenter.previousTrackCommand.isEnabled = notificationInfo.hasPre ?? false
        commandCenter.nextTrackCommand.isEnabled = notificationInfo.hasNext ?? false
    }
    
    func updatePlayState(isPlaying: Bool, position: Int){
        var nowPlayingInfo = MPNowPlayingInfoCenter.default().nowPlayingInfo ?? Dictionary<String,Any>()
        nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = isPlaying ? 1.0:0.0
        nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = position / 1000
        MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
    }
    
}

protocol CommandCenterDelegate{
    func onClickPlay()
    func onClickPrevious()
    func onClickNext()
    func onSeekTo(position:Int)
}
