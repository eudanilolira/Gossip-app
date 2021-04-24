/* Atividade de seleção de conversas

Nessa tela o usuário pode escolher entrar em salas já conhecidas, ou visualizar conversas anteriores
*/
package br.ufpe.cin.gossip

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog

class ChatSelectionActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var peerDisplay: ListView
    private lateinit var newRoomButton: Button



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

        newRoomButton = findViewById(R.id.newRoomButton)
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
        newRoomButton.setOnClickListener {
            var newRoomIntent: Intent = Intent(this, NewRoomActivity::class.java)
            newRoomIntent.putExtra("userName", userName.text.toString())
            startActivity(newRoomIntent)
        }
    }
}