package br.ufpe.cin.gossip

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
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
import androidx.core.widget.addTextChangedListener
import java.net.ServerSocket

class NewRoomActivity : AppCompatActivity() {
    private lateinit var roomNameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var createButton: Button
    var tag: String = "NewRoom"

    private lateinit var registrationListener: NsdManager.RegistrationListener

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupListeners() {
        createButton.setOnClickListener {
            if (it.isEnabled) registerChatService()
        }
        descriptionEdit.addTextChangedListener {
            createButton.isEnabled = descriptionEdit.text.toString().length <= 80
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun registerChatService() {

        val socket = ServerSocket(0)
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = roomNameEdit.text.toString()
            serviceType = "_gossip._tcp"
            port = socket.localPort
            setAttribute("longtext", descriptionEdit.text.toString())
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(tag, "Error $errorCode on Register")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(tag, "Service failed to unregister")
            }

            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                GossipApplication.roomName = serviceInfo?.serviceName.toString()
                Log.d(tag, "Room registerd as ${GossipApplication.roomName}")

                GossipApplication.roomServer = RoomServer(socket)
                var newIntent = Intent (applicationContext, ServerRoomActivity::class.java)
                newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(newIntent)
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                Log.d(tag, "Service unregistered")
            }
        }

        GossipApplication.nsdManager.registerService(
            serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener
        )
    }
}