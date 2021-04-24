package br.ufpe.cin.gossip

import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class ChatSelectionActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var peerDisplay: ListView
    private lateinit var p2pManager: WifiP2pManager
    private lateinit var p2pChannel: WifiP2pManager.Channel
    private lateinit var wifiReceiver: WifiDirectBroadcastReceiver
    private lateinit var p2pIntentFilter: IntentFilter

    private val FINE_LOCATION_RQ_CODE: Int = 1

    private lateinit var peerListListener: WifiP2pManager.PeerListListener
    private var peerList = mutableListOf<WifiP2pDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_selection)
        startComponents()
        setUpListeners()
    }

    private fun startComponents () {
        userName = findViewById(R.id.userNameDisplay)
        userName.text = intent.extras!!.getString("userName", "User Device")

        peerDisplay = findViewById(R.id.peerDisplay)

        p2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        p2pChannel = p2pManager.initialize(this, Looper.getMainLooper(), null)
        wifiReceiver = WifiDirectBroadcastReceiver(p2pManager, p2pChannel, this)

        p2pIntentFilter = IntentFilter()
        p2pIntentFilter.apply {
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        }

        peerListListener = object : WifiP2pManager.PeerListListener {

            override fun onPeersAvailable(peers: WifiP2pDeviceList) {
                if (peers.deviceList != peerList) {
                    peerList.clear()
                    peerList.addAll(peers.deviceList)
                }
            }
        }
    }

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
                setTitle("Change Username")
                setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    if (nameInput.text.toString().isNotEmpty()) userName.text = nameInput.text.toString()
                }
                setNegativeButton("Cancel") {_: DialogInterface, _: Int -> }
            }

            var dialog: AlertDialog = dialogBuilder.create()
            dialog.setView(nameInput)
            dialog.show()

        }
    }

    override fun onResume() {
        registerReceiver(wifiReceiver, p2pIntentFilter)
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(wifiReceiver)
        super.onPause()
    }
}