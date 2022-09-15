import 'package:flutter/services.dart';

class BatteryChannel {
  final MethodChannel _channel = const MethodChannel('my_app/method/battery');
  final EventChannel _event = const EventChannel('my_app/event/battery');

  Future<int?> getBatteryLevel() async {
    return await _channel.invokeMethod<int>('getNativeBatteryLevel');
  }

  Stream get stream => _event.receiveBroadcastStream().cast();
}
