package edu.uw.yrkim98.yama
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat

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
                val manager = createNotificationChannel(context, "com.yama.notify.message", "messageNotifier", "notified messages")
                createNotification(text,number as String,context,"com.yama.notify.message", manager)


            }


        }

    }

    fun createNotificationChannel(context: Context, id: String, name: String, description: String): NotificationManager {

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        return manager
    }

    fun createNotification(message: String, from: String, context: Context, channelID: String, manager: NotificationManager) {
        val intent = Intent("")
        val pIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), intent, 0)
        val notification = NotificationCompat.Builder(context,
            channelID)
            .setContentTitle("New Message from " + from)
            .setContentText("Click to View")
            .setSmallIcon(R.mipmap.conversation)
            .setChannelId(channelID)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        manager.notify(101, notification)


    }


}