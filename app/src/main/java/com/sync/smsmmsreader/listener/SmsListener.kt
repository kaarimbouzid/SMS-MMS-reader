package com.sync.smsmmsreader.listener

interface SmsListener {
    fun onSmsReceived(sender: String?, messageBody: String?)
}
