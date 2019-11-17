package edu.uw.yrkim98.yama

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.service.notification.NotificationListenerService
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class RecieveMessage: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var type = intent.getAction()
        if (type == "android.provider.Telephony.SMS_RECEIVED") {
            var bundle = intent.getExtras()
            val message = bundle!!.get("pdus") as Array<Any>
            for(i in 0..message.size -1){
                var smsMessage = SmsMessage.createFromPdu(message[i] as ByteArray, bundle.getString("format"))
                val number = smsMessage.originatingAddress
                val text = smsMessage.messageBody.toString()
                Toast.makeText(context, number + " | " + text, Toast.LENGTH_LONG).show()
                val manager = createNotificationChannel(context, "com.yama.notify.message", "messageNotifier", "notified messages")
                createNotification(text,number as String,context,"com.yama.notify.message", manager)


            }


        }


    }

    fun createNotificationChannel(context: Context, id: String, name: String, description: String): NotificationManager {

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager!!.createNotificationChannel(channel)
        return manager
    }

    fun createNotification(message: String, from: String, context: Context, channelID: String, manager: NotificationManager) {
        val notification = Notification.Builder(context,
            channelID)
            .setContentTitle("Message From " + from)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .build()
        manager.notify(101, notification)

    }


}