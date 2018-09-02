package com.dev.hercat.todo.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.dev.hercat.todo.R
import com.dev.hercat.todo.activity.MainActivity
import org.jetbrains.anko.intentFor

class NotificationService: Service() {


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ProcessTaskThread(applicationContext).start()
        return Service.START_STICKY
    }
}

class ProcessTaskThread(val context: Context): Thread() {


    override fun run() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context)
        builder.setSmallIcon(R.drawable.icon_close_small)
        builder.setContentTitle("ceshi")
        builder.setContentText(("ceshi"))

        while (true) {


            manager.notify(0x23, builder.build())
            Thread.sleep(10000)
        }
    }
}