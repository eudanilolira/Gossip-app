package br.ufpe.cin.gossip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {

    private lateinit var profilePicture: ImageView
    private lateinit var userNameEdit: EditText
    private lateinit var confirmButton: Button
    private var tag: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startComponents()
        setUpListeners()
    }

    private fun startComponents () {
        profilePicture = findViewById(R.id.profilePicture)
        userNameEdit = findViewById(R.id.userNameEdit)
        confirmButton = findViewById(R.id.confirmButton)
        confirmButton.isEnabled = false

    }

    private fun setUpListeners () {
        profilePicture.setOnClickListener {

        }

        userNameEdit.addTextChangedListener {
            confirmButton.isEnabled = userNameEdit.text.toString().isNotEmpty()
        }

        userNameEdit.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    confirmButton.callOnClick()
                    true
                }
                else -> false
            }
        }

        confirmButton.setOnClickListener {
            if (it.isEnabled) {
                var intent: Intent = Intent(this, ChatSelectionActivity::class.java)
                GossipApplication.userName = userNameEdit.text.toString()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }
}