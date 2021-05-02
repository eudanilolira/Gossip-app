/* Atividade de seleção de conversas

Nessa tela o usuário pode escolher entrar em salas já conhecidas, ou visualizar conversas anteriores
*/
package br.ufpe.cin.gossip

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.nfc.Tag
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.net.InetAddress

class ChatSelectionActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var peerDisplay: RecyclerView
    private lateinit var newRoomButton: Button
    private lateinit var searchRoomButton: Button
    private lateinit var profilePicture: ImageView
    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var connectionInfoListener: WifiP2pManager.ConnectionInfoListener

    var tag: String = "ChatSelection"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_selection)
        supportActionBar?.title = "Salas"
        startComponents ()
        setUpListeners ()

    }

    override fun onResume() {
        registerReceiver(GossipApplication.broadcastReceiver, GossipApplication.p2pIntentFilter)
        newRoomButton.isEnabled = !GossipApplication.runningServer

        for (r: RoomItem in GossipApplication.roomList) {
            adapter.add( r )
        }
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(GossipApplication.broadcastReceiver)
        super.onPause()
    }

    private fun startComponents () {
        profilePicture = findViewById(R.id.profilePicture)
        profilePicture.setImageBitmap(GossipApplication.profilePicture)

        peerDisplay = findViewById(R.id.peerDisplay)

        peerDisplay.adapter = adapter

        userName = findViewById(R.id.userNameDisplay)
        userName.text = GossipApplication.userName

        newRoomButton = findViewById(R.id.newRoomButton)
        searchRoomButton = findViewById(R.id.serachRoomButton)

        connectionInfoListener = WifiP2pManager.ConnectionInfoListener { info ->
            val ownerAddress: InetAddress = info.groupOwnerAddress

            if (info.isGroupOwner && info.groupFormed) {
                Log.d(tag, "I'm the owner for some reason")
            }
            else if (info.groupFormed) {
                Log.d(tag, "Connected to $ownerAddress")
                GossipApplication.roomClient?.receiveHostAddress(ownerAddress)
                GossipApplication.roomClient?.start()
                if (GossipApplication.roomClient != null){
                    var newIntent = Intent (applicationContext, ClientRoomActivity::class.java)
                    newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(newIntent)
                }
            }
        }
        GossipApplication.connectionInfoListener = connectionInfoListener

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpListeners () {
        userName.setOnClickListener {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

            val nameInput: EditText = EditText(this)
            var lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams (
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
                    )

            nameInput.layoutParams = lp

            dialogBuilder.apply {
                setTitle("Novo nome de usuário")
                setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    if (nameInput.text.toString().isNotEmpty()) {
                        userName.text = nameInput.text.toString()
                        GossipApplication.userName = userName.text.toString()
                    }
                }
                setNegativeButton("Cancel") {_: DialogInterface, _: Int -> }
            }

            var dialog: AlertDialog = dialogBuilder.create()

            nameInput.imeOptions = EditorInfo.IME_ACTION_SEND
            nameInput.inputType = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME
            nameInput.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEND -> {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).callOnClick()
                        true
                    }
                    else -> false
                }
            }

            dialog.setView(nameInput)
            dialog.show()
        }
        newRoomButton.setOnClickListener {
            if (!GossipApplication.runningServer) {
                var newRoomIntent: Intent = Intent(this, NewRoomActivity::class.java)
                startActivity(newRoomIntent)
            }
            else {
                Toast.makeText(applicationContext, "Você já possui uma sala", Toast.LENGTH_SHORT).show()
            }
        }

        searchRoomButton.setOnClickListener {
            discoverServices()
        }

        adapter.setOnItemClickListener { item, view ->

        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun discoverServices () {
        Log.d(tag, "Discover Services Function Called")

        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(tag, "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.d(tag, "Resolve found $serviceInfo")
                val roomName = serviceInfo.serviceName
                val roomDescription = serviceInfo.attributes?.get("longtext")?.let { String(it) }
                var roomItem = RoomItem(roomName!!, roomDescription!!, serviceInfo?.host, serviceInfo.port)
                if (roomItem !in GossipApplication.roomList) {
                    GossipApplication.roomList.add(roomItem)
                    Handler (Looper.getMainLooper()).post {
                        adapter.add( roomItem )
                    }
                }
            }

        }

        val discoveryListener = object: NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.d(tag, "Failed to start service discovery. Code: $errorCode")
                GossipApplication.nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.d(tag, "Failed to stop service discovery. Code: $errorCode")
                GossipApplication.nsdManager.stopServiceDiscovery(this)
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                Log.d(tag, "Service discovery started")
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                Log.d(tag, "Service discovery stopped")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                Log.d(tag, "Found Service $serviceInfo")
                GossipApplication.nsdManager.resolveService(serviceInfo!!, resolveListener)
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.d(tag, "Service Lost")
                val roomName = serviceInfo.serviceName
                for (roomItem: RoomItem in GossipApplication.roomList) {
                    if (roomItem.roomName == roomName){
                        Handler (Looper.getMainLooper()).post { adapter.remove( roomItem ) }
                    }
                }

            }

        }


        GossipApplication.nsdManager.discoverServices(
            "_gossip._tcp",
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )
    }
}

