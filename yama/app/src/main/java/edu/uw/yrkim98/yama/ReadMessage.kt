package edu.uw.yrkim98.yama

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_read_msg.*

class ReadMessage: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_msg)
        r_button_chatbox_send.setOnClickListener {
            //Start a Message Instance
            var msg = MessageInstance()
            //Set Message Data
            msg.setMessage(
                r_edittext_number.text.toString(),
                r_edittext_chat.text.toString()
            )
            //Create Intent for Message
            val intent = Intent(CreateMessage.ACTION_SMS_STATUS).putExtra(
                "number",
                r_edittext_number.text.toString()
            ).putExtra("message", r_edittext_chat.text.toString())
            //SENDDDDDDDD ITTTTTTTTTTTTTTT
            msg.sendMessage(
                PendingIntent.getBroadcast(applicationContext, 0, intent, 0),
                r_edittext_chat)
            r_edittext_chat.setText("")}
        loadInstance(getIntent())




    }
    fun loadInstance(intent: Intent) {
        r_msg.text = intent.getStringExtra("message")
        r_edittext_number.setText(intent.getStringExtra("from"))
        r_date.text = "Recieved on: " + intent.getStringExtra("date") + " at " +
                intent.getStringExtra("time")


    }
}