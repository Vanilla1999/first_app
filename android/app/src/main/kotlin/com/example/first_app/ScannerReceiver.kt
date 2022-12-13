package com.example.first_app

/**
 * Created by kartashov_aa on 07.03.17.
 */
interface ScannerReceiver {
    fun onScanComplete(tsd:String, length: Int, barcode: String)
}