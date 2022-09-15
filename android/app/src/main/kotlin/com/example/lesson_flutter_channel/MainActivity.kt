package com.example.lesson_flutter_channel

import android.content.*
import android.os.BatteryManager
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "my_app/method/battery")
            .setMethodCallHandler{
                call, result ->
                if(call.method == "getNativeBatteryLevel") {
                    var batteryLevel = getBatteryLevel();
                    if(batteryLevel != -1){
                        result.success(batteryLevel)
                    }
                    else {
                        result.error("Unvailable", "Battery not available", null)
                    }
                } else {
                    result.notImplemented()
                }
            }
        EventChannel(flutterEngine.dartExecutor.binaryMessenger, "my_app/event/battery").setStreamHandler(BatteryLevelEventChannel(context))
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }
}

class BatteryLevelEventChannel(context: Context): EventChannel.StreamHandler {
    private var chargingStateChangeReceiver: BroadcastReceiver? = null
    private var applicationContext: Context = context



    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        chargingStateChangeReceiver = createChargingStateChangeReceiver(events!!)
        applicationContext.registerReceiver(
            chargingStateChangeReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    override fun onCancel(arguments: Any?) {
        applicationContext.unregisterReceiver(chargingStateChangeReceiver)
        chargingStateChangeReceiver = null
    }

    private fun createChargingStateChangeReceiver(events: EventSink): BroadcastReceiver? {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level * 100 / scale.toFloat()
                events.success(batteryPct);
            }
        }
    }
}
