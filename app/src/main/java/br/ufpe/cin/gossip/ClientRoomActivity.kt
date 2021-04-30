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

        val roomsName = intent.getStringExtra(ChatSelectionActivity.ROOM_KEY)
        supportActionBar?.title = roomsName

    }

    private fun startComponents () {
        chat_recycler_view = findViewById(R.id.chat_recycler_view)
        chat_button = findViewById(R.id.chat_button)
        chat_edit_text = findViewById(R.id.chat_edit_text)

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())

        chat_recycler_view.adapter = adapter



    }
}