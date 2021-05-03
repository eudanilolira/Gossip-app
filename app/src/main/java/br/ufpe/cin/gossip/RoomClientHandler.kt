/**
 * Essa classe existe do lado do servidor, ela gerencia os pacotes recebidos pelo cliente e as envia
 * para as funções específicas do RoomServer da aplicação
 */

package br.ufpe.cin.gossip

import android.graphics.Bitmap
import android.util.Log
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
    var profilePicture: Bitmap? = null

    override fun run() {
        Log.d(tag, "Running")
        var buffer = ByteArray(16*1024)
        var bytes: Int
        while (true) {
            try {
                bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    val tmpMsg = String(buffer, 0, bytes)
                    val map = JSONObject(tmpMsg).toMap()
                    Log.d(tag, "Message Received: $map")
                    var code = 0
                    when(map["packetType"].toString())
                    {
                        "message" -> {
                            code = ClientRoomActivity.MESSAGE_RECEIVED
                        }
                        "handshake" -> {
                            code = ClientRoomActivity.HANDSHAKE
                            if (userName == "") {
                                userName = map["userName"].toString()
                                sendInfo()
                            }
                        }
                        "updatePicture" -> {
                            code = ClientRoomActivity.CHANGE_PICTURE
                        }
                        "updateUsername" -> {
                            code = ClientRoomActivity.CHANGE_USERNAME
                        }
                        "leave" -> {
                            code = ClientRoomActivity.LEAVE_ROOM
                        }
                        "file" -> {
                            code = ClientRoomActivity.FILE
                        }
                    }
                    serverRoomActivity.handler.obtainMessage( code, map ).sendToTarget()
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun writeToSocket(mapMessage: Map<String, String>) {
        val msgString = JSONObject(mapMessage).toString()
        Thread {
            try {
                outputStream.write(msgString.toByteArray())
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

    }

    private fun sendInfo () {
        val serverInfo: Map<String, String> = mapOf(
            "port" to GossipApplication.roomServer?.serverImageReceiverPort.toString(),
            "ype" to "serverInfo"
        )
        writeToSocket(serverInfo)
    }
}