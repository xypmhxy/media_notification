import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';
import 'package:media_notification/on_media_button_callback.dart';

import 'media_notification_platform_interface.dart';

/// An implementation of [MediaNotificationPlatform] that uses method channels.
class MethodChannelMediaNotification extends MediaNotificationPlatform {
  OnMediaButtonCallback? _mediaButtonCallback;

  MethodChannelMediaNotification() {
    methodChannel.setMethodCallHandler(_methodHandle);
  }

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
  Future<bool> updatePlayState(bool isPlaying, {int? position}) async {
    final result =
        await methodChannel.invokeMethod<bool>('updatePlayState', {"isPlaying": isPlaying, "position": position});
    return result ?? false;
  }

  @override
  Future<bool> updatePosition(int timeMs) async {
    final result = await methodChannel.invokeMethod<bool>('updatePosition', timeMs);
    return result ?? false;
  }

  @override
  Future<bool> updateSwitchButtonEnable(bool? isPreviousEnable, bool? isNextEnable) async {
    final result = await methodChannel.invokeMethod<bool>('updateSwitchButtonEnable', {
      'isPreviousEnable': isPreviousEnable,
      'isNextEnable': isNextEnable,
    });
    return result ?? false;
  }

  @override
  Future<bool> setOnMediaButtonCallback(OnMediaButtonCallback callback) async {
    _mediaButtonCallback = callback;
    return false;
  }

  Future<void> _methodHandle(MethodCall call) async {
    final method = call.method;
    switch (method) {
      case 'previous':
        _mediaButtonCallback?.onPrevious();
        break;
      case 'play':
        _mediaButtonCallback?.onPlayPause();
        break;
      case 'next':
        _mediaButtonCallback?.onNext();
        break;
      case 'seekTo':
        _mediaButtonCallback?.onSeek(call.arguments as int);
        break;
    }
  }
}
