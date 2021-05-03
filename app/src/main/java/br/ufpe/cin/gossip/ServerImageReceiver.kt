package br.ufpe.cin.gossip

import android.util.Log
import java.io.ByteArrayOutputStream
import java.net.ServerSocket
import java.net.Socket

class ServerImageReceiver (private val serverSocket: ServerSocket): Thread () {
    private val running = true

    private val tag = "SIR"

    override fun run() {
        Log.d(tag, "ServerImageReceiver Started")
        while (running) {
            val clientSocket = serverSocket.accept()
            Log.d(tag, "Connection for file received")

            val clientThread = Thread { receiveFile(socket = clientSocket) }.apply { start() }
        }
    }

    private fun receiveFile (socket: Socket) {
        Log.d(tag, "File Received")
        val inputStream = socket.getInputStream()

        var b = ByteArrayOutputStream()
        var byteArray = ByteArray(1024)
        while (true) {
            val r = inputStream.read(byteArray)
            if (r < 0 ) break;
            b.write(byteArray)
        }
        socket.close()
        Log.d(tag, String(b.toByteArray()))
    }
}