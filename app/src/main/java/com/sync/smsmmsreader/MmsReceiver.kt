@file:Suppress("DEPRECATION", "UNUSED_VARIABLE", "SpellCheckingInspection", "SameParameterValue",
    "PrivatePropertyName", "KotlinConstantConditions"
)

package com.sync.smsmmsreader

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sync.smsmmsreader.api.APIService
import com.sync.smsmmsreader.listener.MmsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit


class MmsReceiver (
    private val mmsListener: MmsListener = object : MmsListener {
        override fun onMmsReceived(sender: String?, messageBody: String?) {}
    }
) :  BroadcastReceiver() {
    private val DEBUG_TAG = javaClass.simpleName.toString()
    private val ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED"
    private val MMS_DATA_TYPE = "application/vnd.wap.mms-message"

    // Retrieve MMS
    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val type = intent.type
        if ((action == ACTION_MMS_RECEIVED) && (type == MMS_DATA_TYPE)) {
            val mmsUri = Uri.parse("content://mms")
            val projection = arrayOf("_id", "sub", "date")
            val selection = null
            val selectionArgs = null
            val sortOrder = "date DESC"
            val cursor = context.contentResolver.query(mmsUri, projection, selection, selectionArgs, sortOrder)
            // check if Cursor not null
            if (cursor != null) {
                // parse Cursor
                if (cursor.moveToFirst()) {
                    var senderPhoneNumber: String? = null
                    var textData: String? = null
                    // do {
                        val messageId = cursor.getString(cursor.getColumnIndex("_id"))
                        Log.d(DEBUG_TAG, messageId)
                        // Phone Number
                        val addrUri = Uri.parse("content://mms/$messageId/addr")
                        val addrProjection = arrayOf("address")
                        val addrSelection = "type=137" // 137 represents the sender's type
                        val addrSelectionArgs = null
                        val addrCursor = context.contentResolver.query(addrUri, addrProjection, addrSelection, addrSelectionArgs, null)

                        if (addrCursor != null) {
                            if (addrCursor.moveToFirst()) {
                                senderPhoneNumber = addrCursor.getString(addrCursor.getColumnIndex("address"))
                            }
                            addrCursor.close()
                        }
                        Log.d(DEBUG_TAG, "phone num: ${senderPhoneNumber!!}")

                        // text Data
                        val partsUri = Uri.parse("content://mms/$messageId/part")
                        val partsProjection = arrayOf("_id", "text")
                        val partsSelection = "ct='text/plain'"
                        val partsSelectionArgs = null
                        val partsCursor = context.contentResolver.query(partsUri, partsProjection, partsSelection, partsSelectionArgs, null)

                        if (partsCursor != null) {
                            if (partsCursor.moveToFirst()) {
                                textData = partsCursor.getString(partsCursor.getColumnIndex("text"))
                            }
                            partsCursor.close()
                        }
                        if (textData != null) {
                            Log.d(DEBUG_TAG, "Text Data: $textData")
                        } else {
                            Log.d(DEBUG_TAG, "No Text DATA")
                        }
                    // } while (cursor.moveToNext())

                    // Fetch Received MMS To List
                    mmsListener.onMmsReceived(senderPhoneNumber.toString(), textData.toString())
                    // initalise Request Service
                    sendMMSData(senderPhoneNumber, textData.toString())
                }
                cursor.close()
            }
        }
    }

    private fun sendMMSData(senderPhoneNumber: String, textData: String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sendmms.online/API/V2mms/")
            .build()

        val service = retrofit.create(APIService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("phoneNumber", senderPhoneNumber)
        jsonObject.put("type","MMS")
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
    }
}