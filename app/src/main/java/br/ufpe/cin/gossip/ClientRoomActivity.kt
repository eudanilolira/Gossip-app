package br.ufpe.cin.gossip

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.io.File

class ClientRoomActivity : AppCompatActivity() {

    lateinit var chat_recycler_view: RecyclerView
    lateinit var chat_button: Button
    lateinit var chat_edit_text: EditText

    lateinit var uploadImageButton: Button
    var adapter = GroupAdapter<GroupieViewHolder>()

    private var tag = "ClientRoomActivity"

    lateinit var handler: Handler

    companion object {
        // Message code type
        val HANDSHAKE = 1
        val MESSAGE_RECEIVED = 2
        val CHANGE_PICTURE = 3
        val CHANGE_USERNAME = 4
        val LEAVE_ROOM = 5
        val HELLO = 6
        val SERVER_INFO = 7
        val FILE = 8
        val IMAGE = 9

        //File Request codes
        val SELECT_IMAGE = 21

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_room)

        startComponents()
        setupListeners()

        val roomsName = GossipApplication.room?.roomName
        supportActionBar?.title = roomsName

        if (GossipApplication.room != null) {
            GossipApplication.room!!.roomClient?.receiveActivity(this)
        }

    }

    private fun startComponents () {
        chat_recycler_view = findViewById(R.id.chat_recycler_view)
        chat_recycler_view.adapter = adapter

        chat_button = findViewById(R.id.chat_button)
        chat_edit_text = findViewById(R.id.chat_edit_text)

        uploadImageButton = findViewById(R.id.uploadImage)

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
                    LEAVE_ROOM -> {
                        val tmpMsg = "${map["userName"]} saiu da conversa"
                        adapter.add( ChatFromItem(tmpMsg) )
                    }
                    SERVER_INFO -> {
                        GossipApplication.room?.roomClient?.imgReceiverPort = map["port"].toString().toInt()
                        Log.d(tag, map["port"].toString().toInt().toString())
                    }
                }
                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun setupListeners () {
        chat_button.setOnClickListener {
            performSendMessage()
        }

        uploadImageButton.setOnClickListener {
            var newIntent = Intent ()
            newIntent.type = "image/*"
            newIntent.action = Intent.ACTION_PICK
            startActivityForResult(newIntent, SELECT_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null){
            val filePath : Uri = data.data!!
            when (requestCode) {
                SELECT_IMAGE -> {
                    Log.d(tag, "Image Path: $filePath")
                    val imgFile: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    adapter.add( ImageToItem(imgFile) )
                    GossipApplication.room?.roomClient?.sendImage(imgFile)
                }
            }
        }
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