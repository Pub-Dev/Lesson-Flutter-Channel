import 'package:flutter/material.dart';
import 'package:lesson_flutter_channel/channels/battery_channel.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final BatteryChannel _channel = BatteryChannel();

  String batteryLevelLabel = 'Battery is not started';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Text(batteryLevelLabel),
            TextButton(
              onPressed: _getBatteryLevelString,
              child: const Text('Update Battery!!'),
            ),
            StreamBuilder(
              stream: _channel.stream,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  return Text('Battery is ${snapshot.data}%');
                } else {
                  return const Text('Battery is not found');
                }
              },
            ),
          ],
        ),
      ),
    );
  }

  void _getBatteryLevelString() async {
    final batteryLevel = await _channel.getBatteryLevel();
    String label = 'Battery is $batteryLevel%';

    setState(() {
      batteryLevelLabel = label;
    });
  }
}
