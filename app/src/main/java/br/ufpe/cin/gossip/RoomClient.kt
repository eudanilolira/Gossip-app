package br.ufpe.cin.gossip

import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class RoomClient (private var hostAddress: String, private var servicePort: Int) : Thread () {
    private var socket: Socket = Socket()
    private var inputStream: InputStream = socket.getInputStream()
    private var outputStream: OutputStream = socket.getOutputStream()
    private var connected: Boolean = false

    override fun run() {
        try {
            socket.connect(InetSocketAddress(hostAddress, servicePort))
            connected = true
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        if (connected) handShake()
        while (connected) {
            var buffer = ByteArray(1024)
            var bytes: Int
            bytes = inputStream.read(buffer)
            if (bytes > 0) {
                var tmpMsg: String = String(buffer, 0, bytes)
                var receivedJson = JSONObject(tmpMsg).toMap()
                verifyReceivedMessage(receivedJson as Map<String, String>)
            }
        }
    }

    private fun handShake() {
        var myInfo: Map<String, String> = mapOf(
            "userName" to GossipApplication.userName,
            "profilePicture" to ""
        )
        sendMessage(JSONObject(myInfo).toString())
    }

    private fun verifyReceivedMessage(json: Map<String, String>) {
        when (json["packetType"]){
            "message" -> {

            }
            "updatePicture" -> {

            }
            "updateUserName" -> {

            }
            "leave" -> {

            }
        }
    }

    fun sendMessage(message: String) {
        var mapMessage: Map<String, String> = mapOf(
            "packetType" to "message",
            "content" to message
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

}