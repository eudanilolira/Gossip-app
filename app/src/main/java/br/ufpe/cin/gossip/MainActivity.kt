package br.ufpe.cin.gossip

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var profilePicture: ImageView
    private lateinit var userNameEdit: EditText
    private lateinit var confirmButton: Button
    private lateinit var profilePictureButton: Button
    private var tag: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.INTERNET), GossipApplication.INTERNET_RQ
        )

        startComponents()
        setUpListeners()
    }

    private fun startComponents () {
        profilePicture = findViewById(R.id.profilePicture)
        profilePictureButton = findViewById<Button>(R.id.select_image_circle)
        userNameEdit = findViewById(R.id.userNameEdit)
        confirmButton = findViewById(R.id.confirmButton)
        confirmButton.isEnabled = false

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            profilePicture .setImageBitmap(imageBitmap)
            profilePictureButton.alpha = 0F
            GossipApplication.profilePicture = imageBitmap
        }
    }


    private fun setUpListeners () {
        profilePictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
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
                GossipApplication.userName = userNameEdit.text.toString()
                Log.d(tag, GossipApplication.userName)
                var intent: Intent = Intent(this, ChatSelectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }
}