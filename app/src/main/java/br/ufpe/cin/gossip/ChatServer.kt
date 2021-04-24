package br.ufpe.cin.gossip

import android.util.Log
import java.io.IOException
import java.net.ServerSocket

class ChatServer (
    var roomName: String,
    var userName: String
    ) :
    Thread () {

    private lateinit var serverSocket: ServerSocket
    private var serviceType: String = "_http._tcp"
    private val tag: String = "SERVER"

    override fun run() {
        Log.d(tag, "Thread started")
        try {
            serverSocket = ServerSocket(0)
            val servicePort = serverSocket.localPort
            var recordMap: Map<String, String> = mapOf(
                "listenport" to servicePort.toString(),
                "ownername" to userName,
                "roomname" to roomName
            )
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }
}