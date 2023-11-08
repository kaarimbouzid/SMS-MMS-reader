package com.sync.smsmmsreader

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService: Service() {

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null;
    }

}