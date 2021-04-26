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

        discoverServices()
    }

    private fun startComponents () {}

    private fun setUpListeners () {}

    private fun discoverServices () {
        Log.d(tag, "Discover Services Function Called")
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions (
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GossipApplication.FINE_LOCATION_RQ
            )
            return
        }

        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
            Log.d(tag, "DnSdTxtRecord available - $fullDomain, $record, $device")
        }

        val serviceListener = WifiP2pManager.DnsSdServiceResponseListener {
                instanceName, registrationType, srcDevice ->
            Log.d(tag, "$instanceName, $registrationType, $srcDevice")
        }

        GossipApplication.p2pManager.setDnsSdResponseListeners(
            GossipApplication.p2pChannel,
            serviceListener,
            txtListener
        )

        var serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        GossipApplication.p2pManager.addServiceRequest(
            GossipApplication.p2pChannel,
            serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(tag, "Service Request Action Listener Started")
                }

                override fun onFailure(reason: Int) {
                    Log.d(tag, "Service Request Action Listener Failed to Start. Error Code: $reason")
                }

            }
        )

        GossipApplication.p2pManager.discoverServices(
            GossipApplication.p2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(tag, "Started Action Listener")
                }
                override fun onFailure(reason: Int) {
                    Log.d(tag, "Failed to start Action Listener. Error Code: $reason")
                }
            }
        )
    }
}