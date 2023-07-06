import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';

import 'media_notification_platform_interface.dart';

/// An implementation of [MediaNotificationPlatform] that uses method channels.
class MethodChannelMediaNotification extends MediaNotificationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_media_notification_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> updateConfig(NotificationConfig config) async {
    final result = await methodChannel.invokeMethod<bool>('updateConfig', config.toMap());
    return result ?? false;
  }

  @override
  Future<bool> updateNotification(MediaNotificationInfo mediaNotificationInfo) async {
    final result = await methodChannel.invokeMethod<bool>('updateNotification', mediaNotificationInfo.toMap());
    return result ?? false;
  }

  @override
  Future<bool> updatePlayState(bool isPlaying) async {
    return false;
  }

  @override
  Future<bool> updatePosition(int timeMs) async {
    return false;
  }
}
