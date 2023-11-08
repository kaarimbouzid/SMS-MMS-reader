@file:Suppress("DEPRECATION")

package com.sync.smsmmsreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<Any>
            val mmsData = parseSmsMessage(context, pdus)
        }
    }

    private fun parseSmsMessage(context: Context,pdus: Array<Any>): Any {
        for (pdu in pdus) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
            val sender = smsMessage.displayOriginatingAddress
            val messageBody = smsMessage.messageBody
            Log.d("sender",sender.toString())
            Log.d("senderBody",messageBody.toString())
            Toast.makeText(context, messageBody, Toast.LENGTH_LONG).show()
        }
        return "";
    }
}