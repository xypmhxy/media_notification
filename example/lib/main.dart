import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:media_notification/bean/media_notification_info.dart';
import 'package:media_notification/bean/notification_config.dart';
import 'package:media_notification/media_notification.dart';
import 'package:media_notification/on_media_button_callback.dart';
import 'package:video_player/video_player.dart';

import 'video.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> implements OnMediaButtonCallback {
  final _mediaNotificationPlugin = MediaNotification();
  final videoList = [];
  int playIndex = 0;
  VideoPlayerController? _videoPlayerController;
  bool isPlaying = false;
  bool onDrag = false;
  int position = 0;
  int duration = 0;

  @override
  void initState() {
    super.initState();
    videoList.add(Video(
        title: 'Network image Title',
        subTitle: 'VideoSubtitle',
        imagePath: 'https://media.w3.org/2010/05/sintel/poster.png',
        assets: 'assets/video.mp4'));

    videoList.add(Video(
        title: 'Video 1 Title',
        subTitle: 'Video 1 Subtitle',
        imagePath: 'assets://th_video_1.jpg',
        assets: 'assets/video_1.mp4'));

    videoList.add(Video(
        title: 'Video 2 Title',
        subTitle: 'Video 2 Subtitle',
        imagePath: 'assets://th_video_2.jpg',
        assets: 'assets/video_2.mp4'));

    _mediaNotificationPlugin.updateConfig(NotificationConfig(
        appIcon: "resource://mipmap/ic_launcher",
        androidLargeIcon: "resource://drawable/bg_largeicon",
        androidPreIcon: "resource://drawable/ic_pre",
        androidPlayIcon: "resource://drawable/ic_play",
        androidPauseIcon: "resource://drawable/ic_pause",
        androidNextIcon: "resource://drawable/ic_next",
        imageSize: const Size(168, 168)));
    _mediaNotificationPlugin.setOnMediaButtonCallback(this);
    setDataSource(videoList.first);
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
                    child: Stack(
                      children: [
                        VideoPlayer(_videoPlayerController!),
                        Positioned(
                          bottom: 12,
                          child: SizedBox(
                            width: 375,
                            child: Slider(
                              value: position.toDouble(),
                              max: duration.toDouble(),
                              onChangeStart: (value) {
                                onDrag = true;
                              },
                              onChangeEnd: (value) {
                                onSeek(value.toInt());
                                onDrag = false;
                              },
                              onChanged: (double value) {
                                position = value.toInt();
                                setState(() {});
                              },
                            ),
                          ),
                        )
                      ],
                    ),
                  ),
            const SizedBox(
              height: 120,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                ElevatedButton(
                    onPressed: () {
                      onPrevious();
                    },
                    child: const Text('Previous')),
                ElevatedButton(
                    onPressed: () {
                      if (_videoPlayerController!.value.isPlaying) {
                        _videoPlayerController?.pause();
                        _mediaNotificationPlugin.updatePlayState(false,
                            position: _videoPlayerController!.value.position.inMilliseconds);
                      } else {
                        _videoPlayerController?.play();
                        _mediaNotificationPlugin.updatePlayState(true,
                            position: _videoPlayerController!.value.position.inMilliseconds);
                      }
                    },
                    child: Text(isPlaying ? 'Pause' : 'Play')),
                ElevatedButton(
                    onPressed: () {
                      onNext();
                    },
                    child: const Text('Next'))
              ],
            ),
          ],
        ),
      ),
    );
  }

  @override
  void onNext() {
    playIndex += 1;
    if (playIndex >= videoList.length) {
      _mediaNotificationPlugin.updateSwitchButtonEnable(isPreviousEnable: true, isNextEnable: false);
      return;
    }
    setDataSource(videoList[playIndex]);
  }

  @override
  void onPlayPause() {
    if (_videoPlayerController!.value.isPlaying) {
      _videoPlayerController?.pause();
      _mediaNotificationPlugin.updatePlayState(false, position: _videoPlayerController!.value.position.inMilliseconds);
    } else {
      _videoPlayerController?.play();
      _mediaNotificationPlugin.updatePlayState(true, position: _videoPlayerController!.value.position.inMilliseconds);
    }
  }

  @override
  void onPrevious() {
    playIndex -= 1;
    if (playIndex < 0) {
      _mediaNotificationPlugin.updateSwitchButtonEnable(isPreviousEnable: false, isNextEnable: true);
      return;
    }
    setDataSource(videoList[playIndex]);
  }

  @override
  void onSeek(int position) {
    _videoPlayerController?.seekTo(Duration(milliseconds: position));
    _mediaNotificationPlugin.updatePosition(position);
  }

  Future<void> setDataSource(Video video) async {
    await _videoPlayerController?.dispose();
    _videoPlayerController = VideoPlayerController.asset(video.assets);
    _videoPlayerController?.addListener(() {
      final isPlaying = _videoPlayerController?.value.isPlaying ?? false;
      if (this.isPlaying != isPlaying) {
        this.isPlaying = isPlaying;
        setState(() {});
      }

      final position = _videoPlayerController?.value.position.inMilliseconds ?? 0;
      duration = _videoPlayerController?.value.duration.inMilliseconds ?? 0;

      if (position >= duration && duration > 0) {
        _mediaNotificationPlugin.updatePlayState(false);
        onSeek(0);
      }

      if (onDrag == false) {
        this.position = position;
      }

      setState(() {});
    });
    _videoPlayerController?.initialize().then((value) {
      _videoPlayerController?.play();
      setState(() {});
      _mediaNotificationPlugin.updateNotification(MediaNotificationInfo(
          title: video.title,
          subtitle: video.subTitle,
          isPlaying: true,
          isNextEnable: true,
          isPreviousEnable: true,
          duration: _videoPlayerController!.value.duration.inMilliseconds,
          position: 0,
          placeHolderAssets: 'assets/bg_largeicon.jpg',
          imagePath: video.imagePath));
    });
  }
}
