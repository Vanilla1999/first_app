package com.example.first_app

import com.google.gson.GsonBuilder
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

class EventChannelHandler {
    private lateinit var eventChannel: EventChannel
    private lateinit var event: EventChannel.EventSink
    fun startListening(messenger: BinaryMessenger?) {
        eventChannel = EventChannel(messenger, "channel/scanners");
        eventChannel.setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink) {
                    event = eventSink
                }

                override fun onCancel(p0: Any) {
                }
            }
        )
    }

    fun onScanCompite(barcode: String, tsd: String) {
        val json: String = GsonBuilder().create().toJson(Barcode(tsd, barcode))
        event.success(json)
    }
}

data class Barcode(
    val tsd: String,
    val barcode: String
)