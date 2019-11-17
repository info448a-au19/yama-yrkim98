package edu.uw.yrkim98.yama

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.provider.Telephony
import android.telephony.SmsManager
import android.text.Layout
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
           startActivity(
               Intent(applicationContext, CreateMessage::class.java))

        }

        //Setup the recycler view
        val msgRecyclerView = findViewById<RecyclerView>(R.id.msg_recycler_view)
        msgRecyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        msgRecyclerView.adapter = MessageAdapter(generateFakeMsg(), applicationContext)
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



    }

    private fun generateFakeMsg(): List<edu.uw.yrkim98.yama.Message> {
        val from = listOf(
            "bob", "dylan", "deanna", "kent")
        val msg = listOf(
            "This is truly amazing, unexpected...",
            "Yes, yes, yes! It is happening!",
            "Follow our blog to learn more...",
            "Well, it supposed to happen...")
        val times = listOf(
            "13:42",
            "16:16",
            "12:34",
            "20:20")
        val emailList = mutableListOf<edu.uw.yrkim98.yama.Message>()
        for (i in 0..10) {
            emailList.add(
                Message(from.random(), msg.random(), times.random())
            )
        }
        return emailList
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

data class Message(val from: String, val message: String, val time: String)

class MessageAdapter(private val messageList: List<edu.uw.yrkim98.yama.Message>, private val context: Context):
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {


    override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {
        holder.fromText.text = messageList[position].from
        holder.msgText.text = messageList[position].message
        holder.timeText.text = messageList[position].time



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
        val timeText: TextView = view.findViewById(R.id.msg_time)
    }
}

