package br.ufpe.cin.gossip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

class WifiDirectBroadcastReceiver(
    val wifiManager: WifiP2pManager,
    val wifiChannel: WifiP2pManager.Channel,
    private val mainActivity: ChatSelectionActivity
        ): BroadcastReceiver () {

    private var tag: String = "WBR"

    override fun onReceive(context: Context, intent: Intent) {
        var action: String? = intent.action

        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {}
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {}
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {}
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {}
        }
    }

}