package br.ufpe.cin.gossip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {

    private lateinit var profilePicture: ImageView
    private lateinit var userNameEdit: EditText
    private lateinit var confirmButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startComponents()
        setUpListeners()
    }

    private fun startComponents () {
        profilePicture = findViewById(R.id.profilePicture)
        userNameEdit = findViewById(R.id.userName)
        confirmButton = findViewById(R.id.confirmButton)
        confirmButton.isEnabled = false

    }

    private fun setUpListeners () {
        profilePicture.setOnClickListener {

        }

        userNameEdit.addTextChangedListener {
            confirmButton.isEnabled = userNameEdit.text.toString().isNotEmpty()
        }

        confirmButton.setOnClickListener {
            var intent: Intent = Intent(this, ChatSelectionActivity::class.java)
            intent.putExtra("userName", userNameEdit.text.toString())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}