import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';

import 'media_notification_platform_interface.dart';

class MediaNotification {
  Future<String?> getPlatformVersion() {
    return MediaNotificationPlatform.instance.getPlatformVersion();
  }

  Future<bool> updateConfig(NotificationConfig config) {
    return MediaNotificationPlatform.instance.updateConfig(config);
  }

  Future<bool> updateNotification(MediaNotificationInfo mediaNotificationInfo) {
    return MediaNotificationPlatform.instance.updateNotification(mediaNotificationInfo);
  }

  Future<bool> updatePlayState(bool isPlaying) {
    return MediaNotificationPlatform.instance.updatePlayState(isPlaying);
  }

  Future<bool> updatePosition(int timeMs) {
    return MediaNotificationPlatform.instance.updatePosition(timeMs);
  }
}
