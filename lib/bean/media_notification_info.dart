/*
* 作者 Ren
* 时间  2023/7/4 21:12
*/
class MediaNotificationInfo {
  String? title;
  String? subtitle;
  bool? hasNext;
  bool? hasPre;
  bool? isPlaying;
  int? duration;
  int? position;
  double? playSpeed;
  String? imagePath;
  List<int>? imageData;

  MediaNotificationInfo({
    this.title,
    this.subtitle,
    this.hasNext,
    this.hasPre,
    this.isPlaying,
    this.duration,
    this.position,
    this.playSpeed,
    this.imagePath,
    this.imageData,
  });

  Map toMap() {
    return {
      'title': title,
      'subtitle': subtitle,
      'hasNext': hasNext,
      'hasPre': hasPre,
      'isPlaying': isPlaying,
      'duration': duration,
      'position': position,
      'playSpeed': playSpeed,
      'imagePath': imagePath,
      'imageData': imageData
    };
  }
}
