package com.raywenderlich.platform_channel_events

import io.flutter.plugin.common.EventChannel
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {
    private  val networkEventChannel = "platform_channel_events/connectivity"
    private val imageEventChannel = "platform_channel_events/image"
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        EventChannel(flutterEngine.dartExecutor.binaryMessenger,
                imageEventChannel).setStreamHandler(ImageStreamHandler(this))
        EventChannel(flutterEngine.dartExecutor.
        binaryMessenger, networkEventChannel).setStreamHandler(NetworkStreamHandler(this))
    }

}
