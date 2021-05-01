package br.ufpe.cin.gossip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ServerRoomActivity : AppCompatActivity() {
    private lateinit var lastReceivedMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_room)

        GossipApplication.roomServer?.receiveActivity(this)

        startComponents()
    }

    private fun startComponents() {
        lastReceivedMessage = findViewById(R.id.lastReceivedMessage)
    }

    fun changeText(msg: String) {
        lastReceivedMessage.text = msg
    }
}