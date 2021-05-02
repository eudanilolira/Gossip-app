package br.ufpe.cin.gossip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import javax.net.ssl.HandshakeCompletedEvent

class ClientRoomActivity : AppCompatActivity() {

    lateinit var chat_recycler_view: RecyclerView
    lateinit var chat_button: Button
    lateinit var chat_edit_text: EditText
    var adapter = GroupAdapter<GroupieViewHolder>()

    private var tag = "ClientRoomActivity"

    lateinit var handler: Handler

    companion object {
        val HANDSHAKE = 1
        val MESSAGE_RECEIVED = 2
        val CHANGE_PICTURE = 3
        val CHANGE_USERNAME = 4
        val LEAVE_ROOM = 5
        val HELLO = 6

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_room)

        startComponents()

        val roomsName = GossipApplication.room?.roomName
        supportActionBar?.title = roomsName

        if (GossipApplication.room != null) {
            GossipApplication.room!!.roomClient?.receiveActivity(this)
        }

    }

    private fun startComponents () {
        chat_recycler_view = findViewById(R.id.chat_recycler_view)
        chat_button = findViewById(R.id.chat_button)
        chat_edit_text = findViewById(R.id.chat_edit_text)

//        adapter.add(ChatFromItem("Boa noite grupo"))
//        adapter.add(ChatToItem(" Fala galera"))
//        adapter.add(ChatFromItem("Boa noite pra quem??"))

        chat_button.setOnClickListener {
            performSendMessage()
        }

        chat_recycler_view.adapter = adapter

        handler = Handler(object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                val map = msg.obj as Map<String, String>
                when (msg.what)
                {
                    MESSAGE_RECEIVED -> {
                        val tmpMsg = "${map["userName"]}: ${map["content"]}"
                        adapter.add( ChatFromItem(tmpMsg) )
                    }
                    HELLO -> {
                        val tmpMsg = "${map["userName"]} entrou na conversa"
                        adapter.add( ChatFromItem(tmpMsg) )
                    }
                }
                return true
            }

        })
    }

    private fun performSendMessage() {
        val message = chat_edit_text.text.toString()
        GossipApplication.room?.roomClient?.sendMessage(message)
        adapter.add(ChatToItem(message))

        chat_edit_text.text.clear()
        chat_edit_text.clearFocus()
        chat_recycler_view.scrollToPosition(adapter.itemCount)
    }
}