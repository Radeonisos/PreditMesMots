package com.entreprise.davfou.preditmesmots

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by tensa on 06/06/2018.
 */

class DbWorkerThread(threadName: String): HandlerThread(threadName){
    private lateinit var mWorkerHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWorkerHandler = Handler(looper)
    }

    fun postTask(task: Runnable){
        mWorkerHandler.post(task)
    }
}