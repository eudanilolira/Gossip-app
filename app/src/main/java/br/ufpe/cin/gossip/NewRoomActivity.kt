package br.ufpe.cin.gossip

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import java.net.ServerSocket

class NewRoomActivity : AppCompatActivity() {
    private lateinit var roomNameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var createButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_room)

        startComponents()
        setupListeners()
    }

    private fun startComponents () {
        roomNameEdit = findViewById(R.id.roomNameEdit)
        descriptionEdit = findViewById(R.id.roomDescriptionEdit)
        createButton = findViewById(R.id.createRoomButton)
    }

    private fun setupListeners () {
        createButton.setOnClickListener {
            if (it.isEnabled) {
                createRoomService()
            }
        }
    }

    private fun createRoomService () {
        val roomSocket: ServerSocket = ServerSocket(0)
        var serviceInfoMap: Map<String, String> = mapOf(
            "roomName" to roomNameEdit.text.toString(),
            "servicePort" to roomSocket.localPort.toString()
        )

        var serviceInfo: WifiP2pDnsSdServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "_gossip._$roomNameEdit", "_gossip.tcp", serviceInfoMap
        )

    }

}