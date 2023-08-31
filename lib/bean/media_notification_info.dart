/*
* 作者 Ren
* 时间  2023/7/4 21:12
*/
class MediaNotificationInfo {
  String? title;
  String? subtitle;
  bool? isNextEnable;
  bool? isPreviousEnable;
  bool? isPlaying;
  int? duration;
  int? position;
  double? playSpeed;
  String? imagePath;
  String? placeHolderAssets;
  List<int>? imageData;

  MediaNotificationInfo({
    this.title,
    this.subtitle,
    this.isNextEnable,
    this.isPreviousEnable,
    this.isPlaying,
    this.duration,
    this.position,
    this.playSpeed,
    this.imagePath,
    this.placeHolderAssets,
    this.imageData,
  });

  Map toMap() {
    return {
      'title': title,
      'subtitle': subtitle,
      'isNextEnable': isNextEnable,
      'isPreviousEnable': isPreviousEnable,
      'isPlaying': isPlaying,
      'duration': duration,
      'position': position,
      'playSpeed': playSpeed,
      'imagePath': imagePath,
      'imageData': imageData
    };
  }
}
