/**
 * Essa classe é o cliente de uma aplicação, existe quando o usuário se conecta a uma sala existente
 * Ela recebe e envia pacotes para o servidor, chamando a função aproriada na Activity para
 * modificar a UI
**/
package br.ufpe.cin.gossip

import android.util.Log
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class RoomClient (private var hostAddress: InetAddress, private var servicePort: Int) : Thread () {
    private lateinit var socket: Socket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private var connected: Boolean = true
    private lateinit var clientRoomActivity: ClientRoomActivity
    var userName: String = ""

    private var tag: String = "RoomClient"

    override fun run() {
        try {
            socket = Socket ()
            socket.connect(InetSocketAddress(hostAddress, servicePort))
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
            Log.i(tag, "Socket connected successfully")
        }
        catch (e: IOException) {
            e.printStackTrace()
            connected = false
            Log.e(tag, "Failed to connect to Socket")
            return
        }
        if (connected) handShake()
        var buffer = ByteArray(1024)
        var bytes: Int
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    var tmpMsg = String(buffer, 0, bytes)
                    val map = JSONObject(tmpMsg).toMap()
                    Log.d(tag, "$map")
                    var code =
                        when(map["packetType"].toString())
                        {
                            "message" -> ClientRoomActivity.MESSAGE_RECEIVED
                            "updatePicture" -> ClientRoomActivity.CHANGE_PICTURE
                            "updateUsername" -> ClientRoomActivity.CHANGE_USERNAME
                            "leave" -> ClientRoomActivity.LEAVE_ROOM
                            "hello" -> ClientRoomActivity.HELLO
                            else -> 0
                        }
                    clientRoomActivity.handler.obtainMessage( code, map ).sendToTarget()
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
                Log.e(tag, "Failed to receive message")
            }
        }
    }

    private fun handShake() {
        var myInfo: Map<String, String> = mapOf(
            "userName" to GossipApplication.userName,
            "profilePicture" to "",
            "packetType" to "handshake"
        )
        writeToSocket(myInfo)
    }

    fun sendMessage(message: String) {
        var messagePacket: Map<String, String> = mapOf(
            "userName" to GossipApplication.userName,
            "packetType" to "message",
            "content" to message
        )
        writeToSocket(messagePacket)
    }

    fun writeToSocket(mapMessage: Map<String, String>) {

        var msgString = JSONObject(mapMessage).toString()
        Thread {
            try {
                outputStream.write(msgString.toByteArray())
                Log.d(tag, "Message send successfully")
            }
            catch (e: IOException) {
                e.printStackTrace()
                Log.e(tag, "Failed to send message")
            }
        }.start()
    }

    fun receiveActivity (activityCompat: ClientRoomActivity) {
        if (!this::clientRoomActivity.isInitialized) {
            clientRoomActivity = activityCompat
        }
    }

    fun closeSocket (){
        if (this.isAlive) this.interrupt()
        socket.close()
    }
}