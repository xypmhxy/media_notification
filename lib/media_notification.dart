import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';
import 'package:media_notification/on_media_button_callback.dart';

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

  Future<bool> updatePlayState(bool isPlaying, {int? position}) {
    return MediaNotificationPlatform.instance.updatePlayState(isPlaying, position: position);
  }

  Future<bool> updatePosition(int timeMs) {
    return MediaNotificationPlatform.instance.updatePosition(timeMs);
  }

  Future<bool> updateSwitchButtonEnable({bool? isPreviousEnable, bool? isNextEnable}) {
    return MediaNotificationPlatform.instance.updateSwitchButtonEnable(isPreviousEnable, isNextEnable);
  }

  Future<bool> setOnMediaButtonCallback(OnMediaButtonCallback callback) {
    return MediaNotificationPlatform.instance.setOnMediaButtonCallback(callback);
  }
}
