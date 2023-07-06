/*
* 作者 Ren
* 时间  2023/7/4 21:15
*/

import 'package:flutter/material.dart';

class NotificationConfig {
  String? appIcon; // only android
  String? androidLargeIcon; // only android
  String? androidSmallIcon; // only android
  String? androidPlayIcon; // android required
  String? androidPauseIcon; // android required
  String? androidPreIcon; // android required
  String? androidNextIcon; // android required
  String? androidPlaceholderImage; // only android
  Size? imageSize;

  NotificationConfig({
    this.appIcon,
    this.androidLargeIcon,
    this.androidSmallIcon,
    this.androidPlayIcon,
    this.androidPauseIcon,
    this.androidPreIcon,
    this.androidNextIcon,
    this.androidPlaceholderImage,
    this.imageSize,
  });

  Map toMap() {
    return {
      'appIcon': appIcon,
      'androidLargeIcon': androidLargeIcon,
      'androidSmallIcon': androidSmallIcon,
      'androidPlayIcon': androidPlayIcon,
      'androidPauseIcon': androidPauseIcon,
      'androidPreIcon': androidPreIcon,
      'androidNextIcon': androidNextIcon,
      'androidPlaceholderImage': androidPlaceholderImage,
      'imageWidth': imageSize?.width,
      'imageHeight': imageSize?.height,
    };
  }
}
