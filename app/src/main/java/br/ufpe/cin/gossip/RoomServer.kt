package br.ufpe.cin.gossip

import android.util.Log
import java.net.ServerSocket

class RoomServer ( private val socket: ServerSocket ) : Thread () {
    var clientSockets = mutableListOf<RoomClientHandler>()
    private var tag: String = "RoomServer"
    lateinit var serverRoomActivity: ServerRoomActivity
    override fun run() {
        var running = true
        while (running) {
            Log.d(tag, "Started Server Activity")
            var clientSocket = socket.accept()
            var clientHandler = RoomClientHandler(clientSocket).apply { start() }
            clientSockets.add(clientHandler)
            Log.d(tag, "Connection receive from ${clientSocket.inetAddress}:${clientSocket.port}")
        }
    }

    fun receiveActivity (activity: ServerRoomActivity) {
        if (!this::serverRoomActivity.isInitialized) serverRoomActivity = activity
    }

    fun broadcastMessage (messageMap: Map<String, String>, sender: String = "") {
        for (roomClientHandler: RoomClientHandler in clientSockets) {
            if (sender == "" || roomClientHandler.userName != sender) {
                Log.d(tag, "Message\"$messageMap\" sent to ${roomClientHandler.userName}")
                roomClientHandler.writeToSocket(messageMap)
            }
        }
    }

    fun sendMessage(message: String) {
        var messagePacket: Map<String, String> = mapOf(
            "userName" to GossipApplication.userName,
            "packetType" to "message",
            "content" to message
        )
        Log.d(tag, "Username: ${GossipApplication.userName}")
        broadcastMessage(messagePacket)
    }

}