import 'package:flutter_test/flutter_test.dart';
import 'package:media_notification/media_notification.dart';
import 'package:media_notification/media_notification_platform_interface.dart';
import 'package:media_notification/media_notification_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMediaNotificationPlatform
    with MockPlatformInterfaceMixin
    implements MediaNotificationPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MediaNotificationPlatform initialPlatform = MediaNotificationPlatform.instance;

  test('$MethodChannelMediaNotification is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMediaNotification>());
  });

  test('getPlatformVersion', () async {
    MediaNotification mediaNotificationPlugin = MediaNotification();
    MockMediaNotificationPlatform fakePlatform = MockMediaNotificationPlatform();
    MediaNotificationPlatform.instance = fakePlatform;

    expect(await mediaNotificationPlugin.getPlatformVersion(), '42');
  });
}
