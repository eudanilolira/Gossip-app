package br.ufpe.cin.gossip

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class FindRoomActivity : AppCompatActivity() {
    private var tag: String = "FindRoomActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_room)

    }

    private fun startComponents () {}

    private fun setUpListeners () {}

}