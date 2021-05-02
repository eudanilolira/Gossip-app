package br.ufpe.cin.gossip

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.aware.PeerHandle
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat

class ServerRoomActivity : AppCompatActivity() {
    private lateinit var lastReceivedMessage: TextView

    private val tag: String = "ServerRoomActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_room)

        GossipApplication.roomServer?.receiveActivity(this)

        startComponents()
        discoverPeers()
    }

    private fun startComponents() {
        lastReceivedMessage = findViewById(R.id.lastReceivedMessage)
    }

    fun changeText(msg: String) {
        lastReceivedMessage.text = msg
    }

    private fun discoverPeers () {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            GossipApplication.p2pManager.discoverPeers(
                GossipApplication.p2pChannel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(tag, "Peer discovery started")
                    }

                    override fun onFailure(reason: Int) {
                        Log.d(tag, "Peer discovery failed")
                    }

                }
            )
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GossipApplication.FINE_LOCATION_RQ)
        }
    }
}