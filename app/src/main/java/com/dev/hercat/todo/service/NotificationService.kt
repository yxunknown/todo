package com.dev.hercat.todo.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.dev.hercat.todo.R
import com.dev.hercat.todo.activity.MainActivity
import com.dev.hercat.todo.activity.db
import com.dev.hercat.todo.tools.formatDate
import com.dev.hercat.todo.tools.getDate
import com.dev.hercat.todo.tools.minus
import org.jetbrains.anko.intentFor

class NotificationService : Service() {


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ProcessTaskThread(applicationContext).start()
        return Service.START_STICKY
    }
}

class ProcessTaskThread(val context: Context) : Thread() {


    override fun run() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_main_icon)
                .setContentTitle(context.resources.getString(R.string.notification_title))
        val intent = context.intentFor<MainActivity>()
        val taskStatusBuilder = TaskStackBuilder.create(context)
        taskStatusBuilder.addParentStack(MainActivity::class.java)
        taskStatusBuilder.addNextIntent(intent)
        val pendingIntent = taskStatusBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val notificationContent = context.resources.getString(R.string.notification_content)
        while (true) {
            val tasks = context.db.selectTodoByDate(formatDate().substringBeforeLast(" "))
            for (task in tasks) {
                val times = getDate(task.doTime) - getDate(formatDate())
                if (times in 0..15) {
                    builder.setContentText(String.format(notificationContent, task.name, task.desc, task.doTime))
                    manager.notify(0x23, builder.build())
                }
            }
            Thread.sleep(1 * 60 * 1000)
        }
    }
}