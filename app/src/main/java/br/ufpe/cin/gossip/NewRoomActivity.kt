package br.ufpe.cin.gossip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NewRoomActivity : AppCompatActivity() {
    private lateinit var roomNameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var createButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_room)

        startComponents()
    }

    private fun startComponents() {
        roomNameEdit = findViewById(R.id.roomNameEdit)
        descriptionEdit = findViewById(R.id.roomDescriptionEdit)
        createButton = findViewById(R.id.createRoomButton)
    }
}