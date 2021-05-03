package br.ufpe.cin.gossip

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

class ServerImageReceiver (private val serverSocket: ServerSocket): Thread () {
    private val running = true

    private val tag = "SIR"

    override fun run() {
        Log.d(tag, "ServerImageReceiver Started")
        while (running) {
            val clientSocket = serverSocket.accept()
            val clientThread = Thread { receiveFile(socket = clientSocket) }.apply { start() }
        }
    }

    private fun receiveFile (socket: Socket) {
        val inputStream = socket.getInputStream()
        val buffer = ByteArray(1024)

        try {
            val i = socket.getInputStream().read(buffer)
            val map = JSONObject(String(buffer, 0, i)).toMap()
            Log.d(tag, "File Received")
            val img: Bitmap = BitmapFactory.decodeStream(inputStream)
            socket.close()

            when (map["type"]) {
                "profilePicture" -> {
                    GossipApplication.roomServer?.updatePicture(
                        map["userName"].toString(), img
                    )
                }
                "image" -> {
                    Log.d(tag, "Image received from ${map["userName"]}")
                    Handler (Looper.getMainLooper()).post {
                        GossipApplication.roomServer?.serverRoomActivity?.changePicture(img)
                    }
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}