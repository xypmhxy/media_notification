import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';
import 'package:media_notification/media_notification.dart';
import 'package:media_notification/on_media_button_callback.dart';
import 'package:video_player/video_player.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> implements OnMediaButtonCallback{
  final _mediaNotificationPlugin = MediaNotification();
  VideoPlayerController? _videoPlayerController;
  bool isPlaying = false;

  @override
  void initState() {
    super.initState();
    _mediaNotificationPlugin
        .updateConfig(NotificationConfig(
      appIcon: "resource://mipmap/ic_launcher",
      androidLargeIcon: "resource://drawable/bg_largeicon",
    ))
        .then((value) {
      print('设置config结果 $value');
      // _mediaNotificationPlugin.showNotification();
    });
    _mediaNotificationPlugin.setOnMediaButtonCallback(this);
    _videoPlayerController =
        VideoPlayerController.networkUrl(Uri.parse('https://media.w3.org/2010/05/sintel/trailer.mp4'));
    _videoPlayerController?.addListener(() {
      final isPlaying = _videoPlayerController?.value.isPlaying ?? false;
      if (this.isPlaying != isPlaying) {
        this.isPlaying = isPlaying;
        setState(() {});
      }
    });
    _videoPlayerController?.initialize().then((value) {
      _videoPlayerController?.play();
      setState(() {});
      _mediaNotificationPlugin.updateNotification(MediaNotificationInfo(
          title: '强哥',
          subtitle: '帅帅帅',
          isPlaying: true,
          duration: _videoPlayerController!.value.duration.inMilliseconds,
          position: 0,
          imagePath:
              "https://play-lh.googleusercontent.com/shbTr-jFTDLpS27ETKUmOBX0DO8mv847PKAU4srn18y2OJjabXoNKFqIPs7xCN_MrNrb"));
    });
  }

  // https://media.w3.org/2010/05/sintel/trailer.mp4
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            (_videoPlayerController?.value.isInitialized ?? false) == false
                ? const SizedBox(width: 56, height: 56, child: CircularProgressIndicator())
                : AspectRatio(
                    aspectRatio: _videoPlayerController?.value.aspectRatio ?? 1.75,
                    child: VideoPlayer(_videoPlayerController!),
                  ),
            const SizedBox(
              height: 120,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                ElevatedButton(onPressed: () {}, child: const Text('Previous')),
                ElevatedButton(
                    onPressed: () {
                      if (_videoPlayerController!.value.isPlaying) {
                        _videoPlayerController?.pause();
                        _mediaNotificationPlugin.updatePlayState(false);
                      } else {
                        _videoPlayerController?.play();
                        _mediaNotificationPlugin.updatePlayState(true);
                      }
                    },
                    child: Text(isPlaying ? 'Pause' : 'Play')),
                ElevatedButton(onPressed: () {}, child: const Text('Next'))
              ],
            )
          ],
        ),
      ),
    );
  }

  @override
  void onNext() {

  }

  @override
  void onPlayPause() {
    if (_videoPlayerController!.value.isPlaying) {
      _videoPlayerController?.pause();
      _mediaNotificationPlugin.updatePlayState(false);
    } else {
      _videoPlayerController?.play();
      _mediaNotificationPlugin.updatePlayState(true);
    }
  }

  @override
  void onPrevious() {
  }

  @override
  void onSeek(int position) {
    _videoPlayerController?.seekTo(Duration(milliseconds: position));
  }
}
