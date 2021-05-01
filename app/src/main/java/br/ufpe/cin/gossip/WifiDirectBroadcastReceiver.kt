package br.ufpe.cin.gossip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import java.util.jar.Manifest

class WifiDirectBroadcastReceiver(): BroadcastReceiver () {
    private var tag: String = "WBR"

    override fun onReceive(context: Context, intent: Intent) {
        var action: String? = intent.action
        Log.d(tag, "Signal \"$action\" received.")

        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {}
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> { }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                if (GossipApplication.p2pManager == null) return
                var networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true) {
                    GossipApplication.p2pManager.requestConnectionInfo(
                        GossipApplication.p2pChannel, GossipApplication.connectionInfoListener
                    )
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {}
        }
    }
}