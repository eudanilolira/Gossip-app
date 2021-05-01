package br.ufpe.cin.gossip

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.net.ServerSocket

class NewRoomActivity : AppCompatActivity() {
    private lateinit var roomNameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var createButton: Button
    var tag: String = "NewRoom"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_room)
        supportActionBar?.title = "Nova Sala"

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

        if (GossipApplication.servInfo != null) {
            GossipApplication.p2pManager.removeLocalService(
                GossipApplication.p2pChannel, GossipApplication.servInfo,
                object: WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(tag, "Service unregistered")
                    }

                    override fun onFailure(reason: Int) {
                        Log.d(tag, "Failed to unregister service")
                    }

                }
            )
            GossipApplication.p2pManager.removeGroup(
                GossipApplication.p2pChannel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(tag, "Group removed")
                    }
                    override fun onFailure(reason: Int) {
                        Log.d(tag, "Failed to remove Group")
                    }
                }
            )
        }

        val roomSocket = ServerSocket(0)
        var rName = roomNameEdit.text.toString()
        var serviceInfoMap: Map<String, String> = mapOf(
            "roomName" to rName,
            "roomDescription" to descriptionEdit.text.toString(),
            "servicePort" to roomSocket.localPort.toString(),
            "ownerName" to GossipApplication.userName
        )
        Log.d(tag, serviceInfoMap.toString())
        var serviceInfo: WifiP2pDnsSdServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "_${rName}", "_gossip.tcp", serviceInfoMap
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

                    GossipApplication.servInfo = serviceInfo

                    GossipApplication.runningServer = true
                    GossipApplication.roomServer = RoomServer(roomSocket)
                    GossipApplication.roomServer?.start()
                    var newIntent = Intent(applicationContext, ServerRoomActivity::class.java)
                    newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(newIntent)
                }
                override fun onFailure(reason: Int) {
                    Toast.makeText(applicationContext,
                        "Failed to start service. Error code: $reason.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        )
        Log.d(tag, "Button click call executed")
    }
}