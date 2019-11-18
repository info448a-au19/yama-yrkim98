package edu.uw.yrkim98.yama

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_msg.*

class MessageInstance {
    var destination: String? = null
    var text: String? = null

    fun setMessage(dest: String, txt: String) {
        this.destination = dest
        this.text = txt
    }

    fun sendMessage(int: PendingIntent, view: View) {
        var messager = SmsManager.getDefault()
        messager.sendTextMessage(destination, null, text, int, null)
        Snackbar.make(
            view,
            "Sent Message to: " + destination,
            Snackbar.LENGTH_LONG
        ).show()
    }

}

class CreateMessage: AppCompatActivity() {
    companion object{
        const val ACTION_SMS_STATUS = "edu.uw.yrkim98.yama.ACTION_SMS_STATUS"
    }
    val tracker = 1
    private var contactURI: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_msg)


        //On click listener for Send Message button
        button_chatbox_send.setOnClickListener {
            //Start a Message Instance
            var msg = MessageInstance()
            //Set Message Data
            msg.setMessage(
                edittext_number.text.toString(),
                edittext_chat.text.toString()
            )
            //Create Intent for Message
            val intent = Intent(ACTION_SMS_STATUS).putExtra(
                "number",
                edittext_number.text.toString()
            ).putExtra("message", edittext_chat.text.toString())
            msg.sendMessage(PendingIntent.getBroadcast(applicationContext, 0, intent, 0),
                edittext_chat)
            edittext_chat.setText("")}


        //Got help on this from https://www.youtube.com/watch?v=NLRSh-JTFrA
        //On click listener for contacts Selection
        contacts_pic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, tracker)
            }


        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == tracker && resultCode == Activity.RESULT_OK) {
            contactURI = data!!.data
            val list = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val cursor = contentResolver.query(contactURI as Uri, list, null, null, null)
            if (cursor?.moveToFirst() as Boolean) {
                edittext_number.setText(
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
            }
        }

    }


}


