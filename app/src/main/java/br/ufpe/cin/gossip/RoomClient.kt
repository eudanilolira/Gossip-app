/**
 * Essa classe é o cliente de uma aplicação, existe quando o usuário se conecta a uma sala existente
 * Ela recebe e envia pacotes para o servidor, chamando a função aproriada na Activity para
 * modificar a UI
**/
package br.ufpe.cin.gossip

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.logging.Handler
import kotlin.math.log

class RoomClient (private var hostAddress: InetAddress, private var servicePort: Int) : Thread () {
    private lateinit var socket: Socket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private var connected: Boolean = true
    private lateinit var clientRoomActivity: ClientRoomActivity
    var userName: String = ""
    var imgReceiverPort: Int = 0

    var userNameToPictureMap = mutableMapOf<String, Bitmap>()

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
        var buffer = ByteArray(16*1024)
        var bytes: Int
        if (connected) {
            handShake()
            if ( GossipApplication.profilePicture != null )
                sendProfilePicture()
        }
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
                            "leave" -> ClientRoomActivity.LEAVE_ROOM
                            "hello" -> ClientRoomActivity.HELLO
                            "file" -> ClientRoomActivity.FILE
                            "profilePicture" -> {
                                val img: Bitmap = BitmapFactory.decodeStream(inputStream)
                                userNameToPictureMap.put(map["userName"].toString(), img)
                                -1
                            }
                            "image" -> {
                                val img: Bitmap = BitmapFactory.decodeStream(inputStream)
                                android.os.Handler(Looper.getMainLooper()).post {
                                    clientRoomActivity.adapter.add (ChatImageFrom (
                                        map["userName"].toString(), img
                                            ))
                                }
                                -1
                            }
                            else -> 0
                        }
                    if (code != 0 && this::clientRoomActivity.isInitialized) {
                        clientRoomActivity.handler.obtainMessage(code, map).sendToTarget()
                    }
                    else {
                        imgReceiverPort = map["port"].toString().toInt()
                        Log.d(tag, imgReceiverPort.toString())
                    }
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
                Log.e(tag, "Failed to receive message")
            }
        }
    }

    private fun sendProfilePicture() {
        val imgFile = GossipApplication.profilePicture!!
        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        imgFile.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val imgBytes: ByteArray = stream.toByteArray()

        val imgPacket: Map<String, String> = mapOf(
            "type" to "profilePicture",
            "userName" to GossipApplication.userName,
            "length" to imgBytes.size.toString()
        )

        Log.d(tag, "${imgPacket["length"]}")
        sendFileMap(imgPacket, imgBytes)
        Log.d(tag, "Sending Image")
    }

    fun handShake() {
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

    fun sendImage(imgFile: Bitmap) {
        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        imgFile.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val imgBytes: ByteArray = stream.toByteArray()

        val imgPacket: Map<String, String> = mapOf(
            "type" to "image",
            "userName" to GossipApplication.userName,
            "length" to imgBytes.size.toString()
        )

        Log.d(tag, "${imgPacket["length"]}")
        sendFileMap(imgPacket, imgBytes)
        Log.d(tag, "Sending Image")
    }

    private fun writeToSocket(mapMessage: Map<String, String>) {

        val msgString = JSONObject(mapMessage).toString()
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

    private fun sendFileMap(mapMessage: Map<String, String>, file: ByteArray) {
        val msgString = JSONObject(mapMessage).toString().toByteArray()
        Thread {
            val socket = Socket()
            socket.connect(InetSocketAddress(GossipApplication.room?.host, imgReceiverPort))
            socket.getOutputStream().write(msgString)
            Log.d(tag, "Sending ${file.size} bytes")
            socket.getOutputStream().write(file)

            socket.close()
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