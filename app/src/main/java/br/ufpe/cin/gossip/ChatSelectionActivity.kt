/* Atividade de seleção de conversas

Nessa tela o usuário pode escolher entrar em salas já conhecidas, ou visualizar conversas anteriores
*/
package br.ufpe.cin.gossip

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog

class ChatSelectionActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var peerDisplay: ListView
    private lateinit var newRoomButton: Button
    private lateinit var searchRoomButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_selection)
        startComponents()
        setUpListeners()
    }

    override fun onResume() {
        registerReceiver(GossipApplication.broadcastReceiver, GossipApplication.p2pIntentFilter)
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(GossipApplication.broadcastReceiver)
        super.onPause()
    }

    private fun startComponents () {
        userName = findViewById(R.id.userNameDisplay)
        userName.text = GossipApplication.userName

        peerDisplay = findViewById(R.id.peerDisplay)
        newRoomButton = findViewById(R.id.newRoomButton)
        searchRoomButton = findViewById(R.id.searchRoomButton)

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
            var searchRoomIntent: Intent = Intent ( this, FindRoomActivity::class.java)
            startActivity(searchRoomIntent)
        }
    }

}