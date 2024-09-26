package com.raywenderlich.platform_channel_events

import android.app.Activity
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageStreamHandler(private var activity: Activity?) : EventChannel.StreamHandler {
    private var eventSink : EventChannel.EventSink? = null
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events;
        val quality = (arguments as Map<*, *>)["quality"] as Double
        val chunkSize = arguments["chunkSize"] as Int
        dispatchImageEvents(quality, chunkSize)
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
        activity = null
    }

    private fun dispatchImageEvents(quality: Double, chunkSize: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            if(activity == null) return@launch

            //decode the Drawable
            val bitmap = BitmapFactory.decodeResource(activity!!.resources,
                    R.drawable.cute_cat_unsplash)

            // Compress the drawable using the quality passed from Flutter
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, (quality * 100)
                    .toInt(), stream);

            // Convert the compressed image stream to byte array
            val byteArray = stream.toByteArray();

            // Dispatch the first event (which is the size of the array/image)
            withContext(Dispatchers.Main) {
                eventSink?.success(byteArray.size)

                // Split the array into chunks using the chunkSize passed from Flutter
                val parts = byteArray.size / chunkSize
                val chunks = byteArray.toList().chunked(parts)

                // Loop through the chunks and dispatch each chuck to Flutter
                chunks.forEach {
                    // Mimic buffering with a 50 mills delay
                    delay(50)
                    withContext(Dispatchers.Main) {
                        eventSink?.success(it)
                    }
                }

                withContext(Dispatchers.Main) {
                    // Finally, dispatch an event to indicate the end of the image stream
                    eventSink?.success(Constants.eof)
                }
            }
        }
    }
}