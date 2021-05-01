/* Atividade de seleção de conversas

Nessa tela o usuário pode escolher entrar em salas já conhecidas, ou visualizar conversas anteriores
*/
package br.ufpe.cin.gossip

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private lateinit var peerListListener: WifiP2pManager.PeerListListener

    private var device: WifiP2pDevice? = null

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

            if (GossipApplication.roomClient != null ) {
                GossipApplication.roomClient?.closeSocket()
            }

            val roomItem = item as RoomItem
            device = roomItem.device


            var config = WifiP2pConfig()
            config.deviceAddress = roomItem.device.deviceAddress
            config.groupOwnerIntent = 0

            if (
                ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            ) {
                GossipApplication.p2pManager.connect(
                    GossipApplication.p2pChannel, config, object: WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            Log.d(tag, "Connected successfully")
                            GossipApplication.roomClient = RoomClient(roomItem.port, roomItem)
                        }
                        override fun onFailure(reason: Int) {
                            Log.d(tag, "Error $reason while trying to connect")
                        }
                    }
                )
            }
            else {
                Toast.makeText(applicationContext, "Permissão negada", Toast.LENGTH_SHORT).show()
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun discoverServices () {
        Log.d(tag, "Discover Services Function Called")
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions (
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GossipApplication.FINE_LOCATION_RQ
            )
            return
        }

        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
            Log.d(tag, "Room found at: $fullDomain (${device.deviceAddress})")
            val room = RoomItem( record, device )
            if (room !in GossipApplication.roomList) {
                GossipApplication.roomList.add(room)
                adapter.add( room )
            }
        }

        val serviceListener = WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, srcDevice ->
            Log.d(tag, "Found $instanceName")
        }

        GossipApplication.p2pManager.setDnsSdResponseListeners(
            GossipApplication.p2pChannel,
            serviceListener,
            txtListener
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

        GossipApplication.p2pManager.discoverServices (
            GossipApplication.p2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(tag, "Started Action Listener")
                }
                override fun onFailure(reason: Int) {
                    Log.d(tag, "Failed to start Action Listener. Error Code: $reason")
                }
            }
        )
    }
}

