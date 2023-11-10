@file:Suppress("DEPRECATION")

package com.sync.smsmmsreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.sync.smsmmsreader.api.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<Any>
            val mmsData = parseSmsMessage(context, pdus)
        }
    }

    private fun parseSmsMessage(context: Context,pdus: Array<Any>): Any {
        var senderPhoneNumber: String? = null
        var textData: String? = null
        for (pdu in pdus) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
            val sender = smsMessage.displayOriginatingAddress
            val messageBody = smsMessage.messageBody
            Log.d("sender",sender.toString())
            Log.d("senderBody",messageBody.toString())
            senderPhoneNumber = sender.toString()
            textData = messageBody.toString()
            Toast.makeText(context, messageBody, Toast.LENGTH_LONG).show()
        }
        // initalise Request Service
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sendmms.online/API/V2mms/")
            .build()

        val service = retrofit.create(APIService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("phoneNumber", senderPhoneNumber)
        jsonObject.put("type","SMS")
        jsonObject.put("data",textData)

        val jsonObjectString = jsonObject.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.sendMMS(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful){
                    Log.e( "RETROFIT_SUCCESS", response.body().toString())
                }else{
                    Log.e("RETORFIT_ERROR",response.body().toString())
                }
            }
        }
        return "";
    }
}