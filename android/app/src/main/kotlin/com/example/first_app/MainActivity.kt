package com.example.first_app


import android.content.Context
import android.os.Bundle
import com.google.gson.GsonBuilder
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

class MainActivity : FlutterActivity(), ScannerReceiver {
    private lateinit var honeywellScanner: HoneywellScanner
    private lateinit var eventHandler :EventChannelHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventHandler = EventChannelHandler()
        honeywellScanner = HoneywellScanner(this, this)
        honeywellScanner.init()
        eventHandler.startListening(flutterEngine!!.dartExecutor.binaryMessenger)
    }

    override fun onResume() {
        super.onResume()
        honeywellScanner.prepare()
    }

    override fun onPause() {
        super.onPause()
        honeywellScanner.pause()
    }

    override fun onDestroy() {
        honeywellScanner.release()
        super.onDestroy()
    }

    private fun sendBroadcast(context: Context, barcode: String, tsd: String) {
//        val intent = Intent("Scanner")
//        intent.putExtra("tsd", tsd)
//        intent.putExtra("barcode", barcode)
//        context.sendBroadcast(intent)
        eventHandler.onScanCompite(barcode,tsd)
    }

    override fun onScanComplete(tsd: String, length: Int, barcode: String) {
        sendBroadcast(this, barcode, tsd)
    }
}

