package br.ufpe.cin.gossip

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.nsd.NsdManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.net.InetAddress

class GossipApplication : Application () {

    companion object {
        var userName: String = ""
        var profilePicture: Bitmap? = null
        var roomName: String = ""

        lateinit var p2pManager: WifiP2pManager
        lateinit var p2pChannel: WifiP2pManager.Channel
        lateinit var broadcastReceiver: WifiDirectBroadcastReceiver
        lateinit var p2pIntentFilter: IntentFilter

        lateinit var nsdManager: NsdManager
        lateinit var nsdDiscoveryListener: NsdManager.DiscoveryListener
        lateinit var registrationListener: NsdManager.RegistrationListener

        var runningServer: Boolean = false

        var FINE_LOCATION_RQ = 1
        var INTERNET_RQ = 2

        var roomServer: RoomServer? = null
        var room: RoomItem? = null
        var roomList: MutableList<RoomItem> = mutableListOf()

        lateinit var connectionInfoListener: WifiP2pManager.ConnectionInfoListener

        fun tearDownServices() {
            if (this::nsdManager.isInitialized && this::nsdDiscoveryListener.isInitialized) {
                nsdManager.stopServiceDiscovery(nsdDiscoveryListener)
            }
            if (this::nsdManager.isInitialized && this::registrationListener.isInitialized) {
                nsdManager.unregisterService(registrationListener)
            }
        }

    }

    override fun onCreate() {
        super.onCreate()

        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager

        p2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        p2pChannel = p2pManager.initialize(applicationContext, Looper.getMainLooper(), null)
        broadcastReceiver = WifiDirectBroadcastReceiver()

        p2pIntentFilter = IntentFilter()
        p2pIntentFilter.apply {
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
    }

}