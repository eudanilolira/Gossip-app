package br.ufpe.cin.gossip

import android.util.Log
import java.net.ServerSocket
import java.net.Socket

class RoomServer ( private val socket: ServerSocket ) : Thread () {
    var clientSockets = mutableListOf<RoomClientHandler>()
    var clientByName: MutableMap<String, RoomClientHandler> = mutableMapOf()
    private var tag: String = "RoomServer"
    private lateinit var roomActivity: ServerRoomActivity
    override fun run() {
        var running = true
        while (running) {
            Log.d(tag, "RoomServer iniciado")
            var client_socket = socket.accept()
            var clientHandler = RoomClientHandler(client_socket)
            clientSockets.add(clientHandler)
            Log.d(tag, "Connection receive from ${client_socket.inetAddress}")
        }
    }

    fun receiveMessage (msg: String, handler: RoomClientHandler) {
        for (client: RoomClientHandler in clientSockets) {
            if (client.clientName != handler.clientName) {
                client.sendMessage(msg)
            }
        }
    }

    fun clientHandshake (clientInfo: Map<String, String>, handler: RoomClientHandler) {
        val name: String = clientInfo["userName"].toString()
        clientByName[name] = handler
        roomActivity.changeText("$name entrou na sala")
    }

    fun receiveActivity (serverRoomActivity: ServerRoomActivity) {
        if (!this::roomActivity.isInitialized) roomActivity = serverRoomActivity
    }

}