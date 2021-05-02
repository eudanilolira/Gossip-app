package br.ufpe.cin.gossip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ClientRoomActivity : AppCompatActivity() {

    lateinit var chat_recycler_view: RecyclerView
    lateinit var chat_button: Button
    lateinit var chat_edit_text: EditText
    var adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_room)
        startComponents()

        val roomsName = GossipApplication.roomClient?.roomItem?.roomName
        supportActionBar?.title = roomsName

        if (GossipApplication.roomClient != null) {
            GossipApplication.roomClient?.receiveActivity(this)
            GossipApplication.roomClient?.start()
        }

    }

    private fun startComponents () {
        chat_recycler_view = findViewById(R.id.chat_recycler_view)
        chat_button = findViewById(R.id.chat_button)
        chat_edit_text = findViewById(R.id.chat_edit_text)

        adapter.add(ChatFromItem("Boa noite grupo"))
        adapter.add(ChatToItem(" Fala galera"))
        adapter.add(ChatFromItem("Boa noite pra quem??"))

        chat_button.setOnClickListener {
            this.performSendMessage()
        }

        chat_recycler_view.adapter = adapter
    }

    private fun performSendMessage() {
        val message = chat_edit_text.text.toString()
        GossipApplication.roomClient?.sendMessage(message)
        adapter.add(ChatToItem(message))

        chat_edit_text.text.clear()
        chat_edit_text.clearFocus()
        chat_recycler_view.scrollToPosition(adapter.itemCount)
    }
}