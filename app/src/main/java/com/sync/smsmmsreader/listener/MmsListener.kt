package com.sync.smsmmsreader.listener

interface MmsListener {
    fun onMmsReceived(sender: String?, messageBody: String?)
}