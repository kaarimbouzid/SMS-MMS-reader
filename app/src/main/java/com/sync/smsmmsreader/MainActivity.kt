@file:Suppress("DEPRECATION", "DUPLICATE_LABEL_IN_WHEN")

package com.sync.smsmmsreader

import android.Manifest
import android.app.ActivityManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private val REQUEST_MMS_PERMISSION = 123

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_SMS),
                    REQUEST_MMS_PERMISSION)
            }
        } else {
            // Permission has already been granted
        }

        val serviceIntent = Intent(this, MyService::class.java)
        startForegroundService(serviceIntent)

        foregroundServiceRunning()
    }

    private fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (MyService::class.java.getName() == service.service.className) {
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
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_SMS)) {
                        } else {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.READ_SMS),
                                REQUEST_MMS_PERMISSION)
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
                    Log.d(TAG, "PERMISSIONS_REQUEST_READ_SMS permission granted")
                } else {
                    Log.d(TAG, "PERMISSIONS_REQUEST_READ_SMS permission denied")
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}