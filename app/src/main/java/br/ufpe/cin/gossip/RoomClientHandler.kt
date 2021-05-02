/**
 * Essa classe existe do lado do servidor, ela gerencia os pacotes recebidos pelo cliente e as envia
 * para as funções específicas do RoomServer da aplicação
 */

package br.ufpe.cin.gossip

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.JsonObject
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class RoomClientHandler (private val clientSocket: Socket) : Thread (){
    private var tag: String  = "RoomClientHandler"
    private var inputStream: InputStream = clientSocket.getInputStream()
    private var outputStream: OutputStream = clientSocket.getOutputStream()
    private var serverRoomActivity: ServerRoomActivity = GossipApplication.roomServer!!.serverRoomActivity
    var userName: String = ""

    override fun run() {
        Log.d(tag, "Running")
        var buffer = ByteArray(1024)
        var bytes: Int
        while (clientSocket != null) {
            try {
                bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    var tmpMsg = String(buffer, 0, bytes)
                    val map = JSONObject(tmpMsg).toMap()
                    Log.d(tag, "Message Received: $map")
                    var code =
                    when(map["packetType"].toString())
                    {
                        "message" -> ClientRoomActivity.MESSAGE_RECEIVED
                        "handshake" -> ClientRoomActivity.HANDSHAKE
                        "updatePicture" -> ClientRoomActivity.CHANGE_PICTURE
                        "updateUsername" -> ClientRoomActivity.CHANGE_USERNAME
                        "leave" -> ClientRoomActivity.LEAVE_ROOM
                        else -> 0
                    }
                    serverRoomActivity.handler.obtainMessage( code, map ).sendToTarget()

                    if (code == ClientRoomActivity.HANDSHAKE && userName == "") {
                        userName = map["userName"].toString()
                    }
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun writeToSocket(mapMessage: Map<String, String>) {
        var msgString = JSONObject(mapMessage).toString()
        Thread {
            try {
                outputStream.write(msgString.toByteArray())
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

    }
}