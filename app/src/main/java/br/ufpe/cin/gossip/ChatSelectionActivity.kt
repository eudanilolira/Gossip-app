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
        GossipApplication.nsdManager.stopServiceDiscovery(GossipApplication.nsdDiscoveryListener)
        super.onPause()
    }

    override fun onDestroy() {
        GossipApplication.nsdManager.stopServiceDiscovery(GossipApplication.nsdDiscoveryListener)
        super.onDestroy()
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
                if (GossipApplication.room != null){
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
        newRoomButton.setOnClickListener {
            if (!GossipApplication.runningServer) {
                var newRoomIntent: Intent = Intent(this, NewRoomActivity::class.java)
                startActivity(newRoomIntent)
            }
        }

        searchRoomButton.setOnClickListener {
            discoverServices()
        }

        adapter.setOnItemClickListener { item, view ->
            val roomItem = item as RoomItem

            if (roomItem.roomClient == null) roomItem.connect()
            if (roomItem != null ) {
                GossipApplication.room = roomItem
                var newIntent = Intent (applicationContext, ClientRoomActivity::class.java)
                startActivity(newIntent)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun discoverServices () {
        Log.d(tag, "Discover Services Function Called")

        GossipApplication.tearDownServices()

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
        GossipApplication.nsdDiscoveryListener = discoveryListener

        GossipApplication.nsdManager.discoverServices(
            "_gossip._tcp",
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )
    }
}

