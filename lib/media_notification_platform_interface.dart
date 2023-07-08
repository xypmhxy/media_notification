import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';
import 'package:media_notification/on_media_button_callback.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'media_notification_method_channel.dart';

abstract class MediaNotificationPlatform extends PlatformInterface {
  /// Constructs a MediaNotificationPlatform.
  MediaNotificationPlatform() : super(token: _token);

  static final Object _token = Object();

  static MediaNotificationPlatform _instance = MethodChannelMediaNotification();

  /// The default instance of [MediaNotificationPlatform] to use.
  ///
  /// Defaults to [MethodChannelMediaNotification].
  static MediaNotificationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MediaNotificationPlatform] when
  /// they register themselves.
  static set instance(MediaNotificationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> updateConfig(NotificationConfig config);

  Future<bool> updateNotification(MediaNotificationInfo mediaNotificationInfo);

  Future<bool> updatePlayState(bool isPlaying);

  Future<bool> updatePosition(int timeMs);

  Future<bool> setOnMediaButtonCallback(OnMediaButtonCallback callback);
}
