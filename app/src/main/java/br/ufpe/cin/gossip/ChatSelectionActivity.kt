/* Atividade de seleção de conversas

Nessa tela o usuário pode escolher entrar em salas já conhecidas, ou visualizar conversas anteriores
*/
package br.ufpe.cin.gossip

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatSelectionActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var peerDisplay: RecyclerView
    private lateinit var newRoomButton: Button
    private lateinit var searchRoomButton: Button
    private val adapter = GroupAdapter<GroupieViewHolder>()

    var tag: String = "ChatSelection"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_selection)

        startComponents ()
        setUpListeners ()

    }

    override fun onResume() {
        if (GossipApplication.runingServer) {
            var newIntent = Intent(this, ServerRoomActivity::class.java)
            newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(newIntent)
        }
        registerReceiver(GossipApplication.broadcastReceiver, GossipApplication.p2pIntentFilter)
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(GossipApplication.broadcastReceiver)
        super.onPause()
    }

    private fun startComponents () {
        peerDisplay = findViewById(R.id.peerDisplay)
        peerDisplay.adapter = adapter

        userName = findViewById(R.id.userNameDisplay)
        userName.text = GossipApplication.userName

        newRoomButton = findViewById(R.id.newRoomButton)
        searchRoomButton = findViewById(R.id.serachRoomButton)
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
            var newRoomIntent: Intent = Intent(this, NewRoomActivity::class.java)
            startActivity(newRoomIntent)
        }

        searchRoomButton.setOnClickListener {
            discoverServices()
        }

//        adapter.setOnItemClickListener {
//
//        }
    }

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
            Log.d(tag, "DnSdTxtRecord available - $fullDomain, $record, $device")
            GossipApplication.roomList.add(
                RoomItem(record["roomName"].toString(), record["roomDescription"].toString(), record["roomImage"].toString(), device )
            )
        }

        val serviceListener = WifiP2pManager.DnsSdServiceResponseListener {
                instanceName, registrationType, srcDevice ->
            Log.d(tag, "$instanceName, $registrationType, $srcDevice")
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

        GossipApplication.p2pManager.discoverServices(
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

