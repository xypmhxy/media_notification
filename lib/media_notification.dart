
import 'media_notification_platform_interface.dart';

class MediaNotification {
  Future<String?> getPlatformVersion() {
    return MediaNotificationPlatform.instance.getPlatformVersion();
  }
}
