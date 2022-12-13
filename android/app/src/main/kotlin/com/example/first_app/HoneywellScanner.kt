package com.example.first_app

import android.content.Context
import ru.tander.tsdbrowser.scanners.AbstractScanner
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.AidcManager
import android.util.Log
import com.honeywell.aidc.InvalidScannerNameException
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.TriggerStateChangeEvent
import com.honeywell.aidc.UnsupportedPropertyException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.honeywell.aidc.ScannerUnavailableException
import io.flutter.embedding.android.FlutterActivity
import java.util.ArrayList
import java.util.HashMap

class HoneywellScanner(
    private var mContext: Context?,
    private var mScannerReceiver: ScannerReceiver?
) :AbstractScanner(), BarcodeReader.BarcodeListener, TriggerListener {
    private var barcodeReader: BarcodeReader? = null
    private var honeyInitialized = false
    private var manager: AidcManager? = null
    override fun init() {
        //Honeywell
        // create the AidcManager providing a Context and a
        // CreatedCallback implementation.
        AidcManager.create(mContext) { aidcManager: AidcManager? ->
            manager = aidcManager
            try {
                barcodeReader = manager!!.createBarcodeReader()
            } catch (e: InvalidScannerNameException) {
                e.printStackTrace()
            }
            initHoneywellScaner()
            setHoneyclaimScanner()
        }
    }

    override fun prepare() {
        initHoneywellScaner()
        setHoneyclaimScanner()
    }

    override fun pause() {
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader!!.release()
        }
    }

    override fun release() {
        //Honeywell
        if (barcodeReader != null) {
            barcodeReader!!.removeBarcodeListener(this)
            barcodeReader!!.removeTriggerListener(this)
        }
        mContext = null
        mScannerReceiver = null
    }

    override fun startRead() {}
    override fun stopRead() {}
    override fun onBarcodeEvent(event: BarcodeReadEvent) {
        Log.e("Barcode", "Barcode event $event")
        (mContext as FlutterActivity?)!!.runOnUiThread {

            // update UI to reflect the data
            val list: MutableList<String> = ArrayList()
            val Barcode: String
            Barcode = if (event.codeId == "}") {
                event.barcodeData.replace("\\u001D".toRegex(), "")
            } else event.barcodeData

            list.add("Barcode data: $Barcode")
            list.add("Character Set: " + event.charset)
            list.add("Code ID: " + event.codeId)
            list.add("AIM ID: " + event.aimId)
            list.add("Timestamp: " + event.timestamp)
            Log.d("ScannerActivity", "onBarcodeEvent: $list")
            mScannerReceiver!!.onScanComplete("honeywell", Barcode.length, Barcode)
        }
    }

    override fun onFailureEvent(event: BarcodeFailureEvent) {
        Log.e("Barcode", "Failure event $event")
    }

    override fun onTriggerEvent(event: TriggerStateChangeEvent) {
        Log.e("Barcode", "Trigger event $event")
    }

    private fun initHoneywellScaner() {
        //--Honeywell
        Log.e("Barcode", "Reader = $barcodeReader, initialized $honeyInitialized")

        if (barcodeReader != null && !honeyInitialized) {

            // register bar code event listener
            barcodeReader!!.addBarcodeListener(this)
            val map = barcodeReader!!.allDefaultProperties
            try {
                barcodeReader!!.setProperty(
                    BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL
                )
            } catch (e: UnsupportedPropertyException) {
                Log.e("Barcode", "Failed to apply properties")
                Toast.makeText(mContext, "Failed to apply properties", Toast.LENGTH_SHORT).show()
            }
            // register trigger state change listener
            barcodeReader!!.addTriggerListener(this)
            val properties: MutableMap<String, Any?> = HashMap()
            for (key in map.keys) {
                properties[key] = map[key]
            }

            //Коды, которые используются согласно оф. информации от сопровождения
            properties[BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER] = false
            properties[BarcodeReader.PROPERTY_QR_CODE_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODE_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_PDF_417_ENABLED] = true
            properties[BarcodeReader.PROPERTY_EAN_13_ENABLED] = true
            properties[BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED] = true
            properties[BarcodeReader.PROPERTY_EAN_13_FIVE_CHAR_ADDENDA_ENABLED] = true
            properties[BarcodeReader.PROPERTY_EAN_13_TWO_CHAR_ADDENDA_ENABLED] = true
            properties[BarcodeReader.PROPERTY_EAN_8_ENABLED] = true
            properties[BarcodeReader.PROPERTY_EAN_8_CHECK_DIGIT_TRANSMIT_ENABLED] = true
            properties[BarcodeReader.PROPERTY_RSS_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODE_39_ENABLED] = true
            //добавлены по запросу SP0099204
            properties[BarcodeReader.PROPERTY_UPC_A_ENABLE] = true
            properties[BarcodeReader.PROPERTY_UPC_A_TWO_CHAR_ADDENDA_ENABLED] = true
            properties[BarcodeReader.PROPERTY_UPC_A_FIVE_CHAR_ADDENDA_ENABLED] = true
            properties[BarcodeReader.PROPERTY_UPC_A_CHECK_DIGIT_TRANSMIT_ENABLED] = true
            // как сказал Олег, данная штука будет обрабатываться на сервере и намн не надо ее отключать.
            properties[BarcodeReader.PROPERTY_UPC_A_TRANSLATE_EAN13] = false
            properties[BarcodeReader.PROPERTY_RSS_EXPANDED_ENABLED] = true //GS1_DATA_BAR_EXPANDED

//            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 48);
//            properties.put(BarcodeReader.PROPERTY_CODE_39_MINIMUM_LENGTH, 0);
//            properties.put(BarcodeReader.PROPERTY_CODE_39_CHECK_DIGIT_MODE, BarcodeReader.CODE_39_CHECK_DIGIT_MODE_NO_CHECK);
//            properties.put(BarcodeReader.PROPERTY_CODE_39_START_STOP_TRANSMIT_ENABLED, false);
//            properties.put(BarcodeReader.PROPERTY_CODE_39_FULL_ASCII_ENABLED, false);
//            properties.put(BarcodeReader.PROPERTY_CODE_39_BASE_32_ENABLED, false);

            //Были, да выключичли
//            properties.put(BarcodeReader.PROPERTY_CODE_93_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_RSS_EXPANDED_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
//            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_ISBT_128_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_UPC_E_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_CODE_11_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_TLC_39_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_MATRIX_25_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_IATA_25_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_STANDARD_25_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_COMPOSITE_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_MAXICODE_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_KOREAN_POST_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_CODABLOCK_A_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_HAX_XIN_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_CODABLOCK_F_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_MSI_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_CHINA_POST_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_TELEPEN_ENABLED, true);
//            properties.put(BarcodeReader.PROPERTY_MICRO_PDF_417_ENABLED, true);


            // Set Max Code 39 barcode length
            //ѣѣ, какого хуя, это здесь было!?
//            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties[BarcodeReader.PROPERTY_CENTER_DECODE] = true
            properties[BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED] =
                true // Enable bad read response
            barcodeReader!!.setProperties(properties)
            //Отладка по конфига
//            map = barcodeReader.getAllProperties();
//            for (Map.Entry<String, Object> e: map.entrySet()) {
//                Log.e("USCANSETTINGS", String.format("\"%s\" = \"%s\"", e.getKey(), e.getValue()));
//            }
            honeyInitialized = true
        }
        Log.e("Barcode", "AFTER Reader = $barcodeReader, initialized $honeyInitialized")
    }

    private fun setHoneyclaimScanner() {
        if (barcodeReader != null) {
            try {
                barcodeReader!!.claim()
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
                Toast.makeText(mContext, "Scanner unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

}