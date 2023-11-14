@file:Suppress("DEPRECATION", "DUPLICATE_LABEL_IN_WHEN", "PrivatePropertyName")

package com.sync.smsmmsreader

import android.Manifest
import android.app.ActivityManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sync.smsmmsreader.adapter.MyAdapter
import com.sync.smsmmsreader.listener.MmsListener
import com.sync.smsmmsreader.listener.SmsListener
import com.sync.smsmmsreader.model.MyItem


class MainActivity : AppCompatActivity(), SmsListener, MmsListener {

    private val REQUEST_MMS_PERMISSION = 123
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemList = mutableListOf<MyItem>()
        val adapter = MyAdapter(itemList)
        recyclerView.adapter = adapter

        // Create an instance of SmsReceiver and pass the listener
        val smsReceiver = SmsReceiver(this)
        val mmsReceiver = MmsReceiver(this)

        // Register the receiver
        val filterSms = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, filterSms)

        // Register the receiver
        val filterMms = IntentFilter("android.provider.Telephony.WAP_PUSH_RECEIVED")
        registerReceiver(mmsReceiver, filterMms)

        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED
        ) {

            if (
                ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)
                &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_MMS)
            ) {
                Log.d("Permission", "Show Permission Prompt")
            } else {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECEIVE_SMS),REQUEST_MMS_PERMISSION)
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECEIVE_MMS),REQUEST_MMS_PERMISSION)
            }
        } else {
            // Permission has already been granted
        }

        // val serviceIntent = Intent(this, MyService::class.java)
        // startForegroundService(serviceIntent)
        // foregroundServiceRunning()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use startForegroundService method
            val serviceIntent = Intent(this, MyService::class.java)
            startForegroundService(serviceIntent)
            foregroundServiceRunning()
            // startForegroundService(intent)
        } else {
            // Use the traditional startService method
            startService(intent)
        }
    }

    override fun onSmsReceived(sender: String?, messageBody: String?) {
        // Add the received SMS to your RecyclerView adapter
        val newSms = MyItem("Tel: $sender", "Type: SMS", messageBody.toString())
        (recyclerView.adapter as MyAdapter).addItem(newSms)
    }

    override fun onMmsReceived(sender: String?, messageBody: String?) {
        // Add the received MMS to your RecyclerView adapter
        val newMms = MyItem("Tel: $sender", "Type: MMS", messageBody.toString())
        (recyclerView.adapter as MyAdapter).addItem(newMms)
    }

    private fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (MyService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_MMS_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted.
                    Log.d(TAG, "REQUEST_MMS_PERMISSION permission granted")

                    // Here, thisActivity is the current activity
                    if (
                        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                        &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED

                    ) {
                        // Permission is not granted
                        if (
                            ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)
                            &&
                            ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_MMS)
                        ) {
                            Log.d("Permission", "Show Permission Prompt")
                        } else {
                            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECEIVE_SMS),REQUEST_MMS_PERMISSION)
                            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECEIVE_MMS),REQUEST_MMS_PERMISSION)
                        }
                    } else {
                        // Permission has already been granted
                    }


                } else {
                    Log.d(TAG, "REQUEST_MMS_PERMISSION permission denied")
                }
                return
            }

            REQUEST_MMS_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "PERMISSIONS_REQUEST_RECEIVE_SMS_MMS permission granted")

                    // val itemList = mutableListOf(
                    //  MyItem("94770763","SMS","HI"),
                    //  MyItem("94770763","SMS","Hello"),
                    // )

                    // val adapter = MyAdapter(itemList)
                    // recyclerView.adapter = adapter

                } else {
                    Log.d(TAG, "PERMISSIONS_REQUEST_RECEIVE_SMS_MMS permission denied")
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}