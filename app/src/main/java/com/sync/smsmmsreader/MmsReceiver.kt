@file:Suppress("DEPRECATION", "UNUSED_VARIABLE", "SpellCheckingInspection", "SameParameterValue",
    "PrivatePropertyName", "KotlinConstantConditions"
)

package com.sync.smsmmsreader

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.sync.smsmmsreader.model.SmsMessage


class MmsReceiver : BroadcastReceiver() {
    private val DEBUG_TAG = javaClass.simpleName.toString()
    private val ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED"
    private val MMS_DATA_TYPE = "application/vnd.wap.mms-message"

    // Retrieve MMS
    override fun onReceive(context: Context, intent: Intent) {
        val result: ContentResolver = context.contentResolver as ContentResolver
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
                val cursor: Cursor? = result.query(uri, null, selection, null, null)
                Log.d(DEBUG_TAG, cursor.toString())

                // val pduType = bundle.getInt("pduType")
                // Log.d(DEBUG_TAG, "pduType $pduType")
                // val buffer2 = bundle.getByteArray("header")
                // val header = String(buffer2!!)
                // Log.d(DEBUG_TAG, "header $header")
                // if (contactId != -1) {
                //    showNotification(contactId, str)
                // }

                // ---send a broadcast intent to update the MMS received in the activity---
                val broadcastIntent = Intent()
                broadcastIntent.action = "MMS_RECEIVED_ACTION"
                broadcastIntent.putExtra("mms", str)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun showNotification(contactId: Int, message: String?) {
        //Display notification...
        print("Message")
        print(contactId)
        print(message)
    }
}