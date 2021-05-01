/**
 * Essa classe é o cliente de uma aplicação, existe quando o usuário se conecta a uma sala existente
 * Ela recebe e envia pacotes para o servidor, chamando a função aproriada na Activity para
 * modificar a UI
**/
package br.ufpe.cin.gossip

import android.util.Log
import androidx.core.app.ActivityCompat
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class RoomClient (private var servicePort: Int, var roomItem: RoomItem? = null) : Thread () {
    private lateinit var hostAddress: InetAddress
    private var socket: Socket = Socket()
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private var connected: Boolean = false
    private lateinit var roomActivity: ClientRoomActivity

    private var tag: String = "RoomClient"

    fun receiveHostAddress(address: InetAddress) {
        if (this::hostAddress.isInitialized) return
        hostAddress = address
    }

    override fun run() {
        try {
            Log.d(tag, "Conectando a $hostAddress:$servicePort")
            socket.connect(InetSocketAddress(hostAddress, servicePort))
            connected = true
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
            Log.d(tag, "Socket conectado com sucesso")
        }
        catch (e: IOException) {
            e.printStackTrace()
            Log.d(tag, "Erro ao conectar socket")
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
                var content = json["content"].toString()
                roomActivity?.adapter.add(
                    ChatFromItem(content)
                )
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
            "content" to message,
            "userName" to GossipApplication.userName
        )
        var msgString = JSONObject(mapMessage).toString()
        Thread {
            try {
                outputStream.write(msgString.toByteArray())
                Log.d(tag, "Mensagem enviada com sucesso")
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun receiveActivity (activityCompat: ClientRoomActivity) {
        if (!this::roomActivity.isInitialized) {
            roomActivity = activityCompat as ClientRoomActivity
        }
    }

    fun closeSocket (){
        if (this.isAlive) this.interrupt()
        socket.close()
    }
}