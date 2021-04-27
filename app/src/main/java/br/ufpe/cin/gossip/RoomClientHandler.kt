

package br.ufpe.cin.gossip

import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class RoomClientHandler (val clientSocket: Socket) : Thread (){
    private var tag: String  = "RoomClientHandler"
    private var inputStream: InputStream = clientSocket.getInputStream()
    private var outputStream: OutputStream = clientSocket.getOutputStream()
    var clientName: String = ""

    override fun run() {
        var buffer = ByteArray(1024)
        var bytes: Int
        while (clientSocket != null) {
            bytes = inputStream.read(buffer)
            if (bytes > 0) {
                var tmpMsg: String = String(buffer, 0, bytes)
                var receivedJson = JSONObject(tmpMsg).toMap()
                verifyReceivedMessage(receivedJson as Map<String, String>)
            }
        }
    }

    fun sendMessage(msg: String) {
        var mapMessage: Map<String, String> = mapOf(
            "packetType" to "message",
            "content" to msg
        )
        var msgString = JSONObject(mapMessage).toString()
        Thread {
            try {
                outputStream.write(msgString.toByteArray())
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    private fun verifyReceivedMessage (message: Map<String, String>) {
        when(message["packetType"]) {
            "message" -> {
                GossipApplication.roomServer?.receiveMessage(message["content"].toString(), this)
            }
            "handshake" -> {
                clientName = message["userName"].toString()
                var clientInfo: Map<String, String> = mapOf(
                    "userName" to message["userName"].toString(),
                    "profilePicture" to ""
                )
                GossipApplication.roomServer?.clientHandshake(clientInfo, this)
            }
            "updatePicture" -> {

            }
            "updateUsername" -> {

            }
            "leave" -> {

            }
        }
    }
}