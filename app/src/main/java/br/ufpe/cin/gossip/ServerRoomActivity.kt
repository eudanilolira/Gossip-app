package br.ufpe.cin.gossip

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.wifi.aware.PeerHandle
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.nio.file.Files

class ServerRoomActivity : AppCompatActivity() {
    private lateinit var lastReceivedMessage: TextView
    private lateinit var answerButton: Button
    private lateinit var imgView: ImageView
    lateinit var handler: Handler

    private val tag: String = "ServerRoomActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_room)

        GossipApplication.roomServer?.receiveActivity(this)

        startComponents()
        setUpListeners()
    }

    private fun startComponents() {
        lastReceivedMessage = findViewById(R.id.lastReceivedMessage)
        answerButton = findViewById(R.id.answerTest)
        imgView = findViewById(R.id.receivedPicture)

        handler = Handler(object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                Log.d(tag, "Handler Called")
                var map = msg.obj as Map<String, String>
                when (msg.what) {
                    ClientRoomActivity.HANDSHAKE -> {
                        Toast.makeText(this@ServerRoomActivity, "${map["userName"]} entrou na sala", Toast.LENGTH_SHORT).show()
                    }
                    ClientRoomActivity.MESSAGE_RECEIVED -> {
                        lastReceivedMessage.text = "${map["userName"]}: ${map["content"]}"
                    }
                    ClientRoomActivity.IMAGE -> {
                        val imgFile: ByteArray = Base64.decode(map["content"].toString(), Base64.DEFAULT)
                        imgView.setImageBitmap(BitmapFactory.decodeByteArray(imgFile, 0, imgFile.size))
                        Log.d(tag, "Image Received")
                    }
                    ClientRoomActivity.FILE -> {}
                }
                return true
            }

        })
    }

    private fun setUpListeners () {
        answerButton.setOnClickListener {
            GossipApplication.roomServer?.sendMessage("Lana rainha, o resto é nadinha!")
        }
    }

}