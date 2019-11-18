package edu.uw.yrkim98.yama

import android.Manifest
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var messageCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
           startActivity(
               Intent(applicationContext, CreateMessage::class.java))

        }


        //Check for send permissions
        var permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.SEND_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.SEND_SMS),100)
        }
        permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.RECEIVE_SMS),
                100
            )
        }
        permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.READ_SMS),
                100
            )
        }

        setRecyclerView()


    }



    override fun onRestart() {
        super.onRestart()
        setRecyclerView()
    }

    fun setRecyclerView() {
        val msgs = getDeviceMsgs()
        this.messageCount = getDeviceMsgs().size
        val msgRecyclerView = msg_recycler_view
        msgRecyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        msgRecyclerView.adapter = MessageAdapter(msgs, applicationContext)
        msgRecyclerView.adapter!!.notifyDataSetChanged()
    }
    fun getDeviceMsgs(): List<edu.uw.yrkim98.yama.Message> {
        var uri = Uri.parse("content://sms/")
        var provider = this.contentResolver
        var cursor = provider.query(uri, null, null, null, null)
        var list = mutableListOf<edu.uw.yrkim98.yama.Message>()
        if (cursor != null /*sometimes this is null.*/){
            if (cursor.moveToFirst()) {

                for (i in 0 until cursor.getCount()) {
                    val creator = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.CREATOR))
                    val isIncoming = if (creator == "edu.uw.yrkim98.yama") false else true
                    print(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.CREATOR)))
                    print(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.PERSON)))
                    var message = edu.uw.yrkim98.yama.Message(
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)),
                        SimpleDateFormat("MM/dd/yyyy").format(
                            Date(cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE)).toLong())),
                        SimpleDateFormat("hh:mm a").format(
                            Date(cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE)).toLong())), isIncoming)
                    list.add(message)
                    cursor.moveToNext()
                }
            }
        }
        return list

    }
    

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}


data class Message(val from: String, val message: String, val date: String, val time:String,
                   val isIncoming: Boolean)

class MessageAdapter(private val messageList: List<edu.uw.yrkim98.yama.Message>, private val context: Context):
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {



    override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {

        holder.fromText.text = if (messageList[position].isIncoming) "From: " +
                messageList[position].from else "Sent: " + messageList[position].from
        holder.msgText.text = messageList[position].message
        holder.dateText.text = messageList[position].date
        var onClickListener = View.OnClickListener {
            val inte = Intent(context, ReadMessage::class.java).apply {
                putExtra("message", messageList[position].message)
                putExtra("from", messageList[position].from)
                putExtra("date", messageList[position].date)
                putExtra("time", messageList[position].time)


            }
            startActivity(context, inte.addFlags(FLAG_ACTIVITY_NEW_TASK), null)
        }
        with(holder.itemView) {
            setOnClickListener(onClickListener)
        }





    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int) : MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message, parent, false))
    }
    override fun getItemCount(): Int {
        return messageList.size
    }
    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromText: TextView = view.findViewById(R.id.message_from)
        val msgText: TextView = view.findViewById(R.id.message_body)
        val dateText: TextView = view.findViewById(R.id.msg_date)
    }

}

