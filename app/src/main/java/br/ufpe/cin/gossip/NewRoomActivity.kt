package br.ufpe.cin.gossip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.net.ServerSocket

class NewRoomActivity : AppCompatActivity() {
    private lateinit var roomNameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var createButton: Button
    var tag: String = "NewRoom"
    private var roomServer: RoomServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_room)

        startComponents()
        setupListeners()
    }

    private fun startComponents() {
        roomNameEdit = findViewById(R.id.roomNameEdit)
        descriptionEdit = findViewById(R.id.roomDescriptionEdit)
        createButton = findViewById(R.id.createRoomButton)
    }

    private fun setupListeners() {
        createButton.setOnClickListener {
            Log.d(tag, "Button routine executing")
            if (it.isEnabled) registerChatService()
        }
    }

    private fun registerChatService() {
        val roomSocket = ServerSocket(0)
        var serviceInfoMap: Map<String, String> = mapOf(
            "roomName" to roomNameEdit.text.toString(),
            "servicePort" to roomSocket.localPort.toString()
        )
        var serviceInfo: WifiP2pDnsSdServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "_${serviceInfoMap["roomName"]}", "_gossip.tcp", serviceInfoMap
        )
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GossipApplication.FINE_LOCATION_RQ
            )
        }
        GossipApplication.p2pManager.addLocalService(
            GossipApplication.p2pChannel,
            serviceInfo,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(applicationContext,
                        "Service registered as ${roomNameEdit.text}",
                        Toast.LENGTH_SHORT).show()
                    roomServer = RoomServer(roomSocket)
                    roomServer?.start()
                }
                override fun onFailure(reason: Int) {
                    Toast.makeText(applicationContext,
                        "Failed to start service. Error code: $reason.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        )

        var serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        GossipApplication.p2pManager.addServiceRequest(
            GossipApplication.p2pChannel,
            serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(tag, "Service Request Action Listener Started")
                }

                override fun onFailure(reason: Int) {
                    Log.d(tag, "Service Request Action Listener Failed to Start. Error Code: $reason")
                }

            }
        )

        Log.d(tag, "Button click call executed")
    }
}