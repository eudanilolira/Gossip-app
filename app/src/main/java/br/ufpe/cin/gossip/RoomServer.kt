package br.ufpe.cin.gossip

import android.graphics.Bitmap
import android.util.Log
import java.net.ServerSocket

class RoomServer ( private val socket: ServerSocket ) : Thread () {
    var clientSockets = mutableListOf<RoomClientHandler>()
    private var tag: String = "RoomServer"
    lateinit var serverRoomActivity: ServerRoomActivity
    lateinit var serverImageReceiver: ServerImageReceiver
    var serverImageReceiverPort: Int = 0

    override fun run() {
        val serverSocket = ServerSocket(0)
        serverImageReceiverPort = serverSocket.localPort
        serverImageReceiver = ServerImageReceiver(serverSocket).apply { start() }

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

    fun broadcastMessage (messageMap: Map<String, String>) {
        val sender = messageMap["userName"].toString()
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

    fun updatePicture(userName: String, img: Bitmap) {
        for (roomClientHandler: RoomClientHandler in clientSockets) {
            if (roomClientHandler.userName == userName) {
                roomClientHandler.profilePicture = img
            }
        }
    }

    fun getProfilePicture(userName: String) : Bitmap? {
        if (userName == "") return null
        for (roomClientHandler: RoomClientHandler in clientSockets) {
            if (roomClientHandler.userName == userName) {
                return roomClientHandler.profilePicture
            }
        }
        return null
    }

}