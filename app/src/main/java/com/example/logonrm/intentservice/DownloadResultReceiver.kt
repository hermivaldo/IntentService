package com.example.logonrm.intentservice

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

/**
 * Created by logonrm on 17/02/2018.
 */
class DownloadResultReceiver(handle: Handler) : ResultReceiver(Handler()) {

    var mReceiver: Receiver? = null

    fun setReceiver(receiver: Receiver){
        this.mReceiver = receiver
    }

    interface Receiver {
        fun onReceiverResult(resultCode: Int, resultData: Bundle)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        if (mReceiver != null){
            mReceiver!!.onReceiverResult(resultCode, resultData)
        }
    }
}