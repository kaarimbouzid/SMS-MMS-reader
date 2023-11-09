@file:Suppress("DEPRECATION", "UNUSED_VARIABLE", "SpellCheckingInspection", "SameParameterValue",
    "PrivatePropertyName", "KotlinConstantConditions"
)

package com.sync.smsmmsreader

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.sync.smsmmsreader.model.SmsMessage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MmsReceiver : BroadcastReceiver() {
    private val DEBUG_TAG = javaClass.simpleName.toString()
    private val ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED"
    private val MMS_DATA_TYPE = "application/vnd.wap.mms-message"

    // Retrieve MMS
    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val type = intent.type
        if ((action == ACTION_MMS_RECEIVED) && (type == MMS_DATA_TYPE)) {
            val bundle = intent.extras
            Log.d(DEBUG_TAG, "bundle $bundle")
            val msgs: Array<SmsMessage>? = null
            val str = ""
            val contactId = -1
            var address: String
            if (bundle != null) {
                val buffer = bundle.getByteArray("data")
                Log.d(DEBUG_TAG, "buffer $buffer")
                var incomingNumber = String(buffer!!)
                var indx = incomingNumber.indexOf("/TYPE")
                if (indx > 0 && indx - 15 > 0) {
                    val newIndx = indx - 15
                    incomingNumber = incomingNumber.substring(newIndx, indx)
                    indx = incomingNumber.indexOf("+")
                    if (indx > 0) {
                        incomingNumber = incomingNumber.substring(indx)
                        Log.d(DEBUG_TAG, "Mobile Number: $incomingNumber")
                    }
                }
                val transactionId = bundle.getInt("transactionId")
                Log.d(DEBUG_TAG, "transactionId $transactionId")
                val uri: Uri = Uri.parse("content://mms/")
                val selection = "_id = $transactionId"
                val cursor: Cursor? = context.contentResolver.query(uri, null, selection, null, null)
                Log.d(DEBUG_TAG, cursor.toString())
                if (cursor!!.moveToFirst()) {
                    do {
                        val partId = cursor.getString(cursor.getColumnIndex("_id"))
                        val type = cursor.getString(cursor.getColumnIndex("ct"))
                        if ("text/plain" == type) {
                            val data = cursor.getString(cursor.getColumnIndex("_data"))
                            var body: String?
                            body = if (data != null) {
                                // implementation of this method below
                                getMmsText(context, partId)
                            } else {
                                cursor.getString(cursor.getColumnIndex("text"))
                            }
                        }
                    } while (cursor.moveToNext())
                }



                // ---send a broadcast intent to update the MMS received in the activity---
                val broadcastIntent = Intent()
                broadcastIntent.action = "MMS_RECEIVED_ACTION"
                broadcastIntent.putExtra("mms", str)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun getMmsText(context: Context, id: String): String? {
        val partURI = Uri.parse("content://mms/part/$id")
        var `is`: InputStream? = null
        val sb = StringBuilder()
        try {
            `is` = context.contentResolver.openInputStream(partURI)
            if (`is` != null) {
                val isr = InputStreamReader(`is`, "UTF-8")
                val reader = BufferedReader(isr)
                var temp: String = reader.readLine()
                while (temp != null) {
                    sb.append(temp)
                    temp = reader.readLine()
                }
            }
        } catch (e: IOException) {
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                }
            }
        }
        return sb.toString()
    }

    private fun showNotification(contactId: Int, message: String?) {
        //Display notification...
        print("Message")
        print(contactId)
        print(message)
    }
}