//
//  ImageUtils.swift
//  Kingfisher
//
//  Created by zjs on 2023/7/21.
//

import Foundation
import Kingfisher

class ImageUtils {
    static func downloadImage(url: URL, name : String, completion: @escaping((_ image: UIImage) -> Void)){
        let kingfisherCache = KingfisherManager.shared.cache
        if kingfisherCache.isCached(forKey: name){
            let memoryData = kingfisherCache.memoryStorage.value(forKey: name)
            if memoryData?.cgImage != nil{
                completion(UIImage.init(cgImage: memoryData!.cgImage!))
                return
            }
            
            if let diskData = try? kingfisherCache.diskStorage.value(forKey: name){
                if let image = UIImage.init(data: diskData){
                    completion(image)
                    return
                }
            }
        }
        
        ImageDownloader.default.downloadImage(with: url, completionHandler: {result in
            do{
                let downloadResult = try result.get()
                guard let cgImage = downloadResult.image.cgImage else { return }
                completion(UIImage.init(cgImage: cgImage))
                kingfisherCache.store(downloadResult.image, forKey: name)
            }catch{
                
            }
        })
    }
    
}
