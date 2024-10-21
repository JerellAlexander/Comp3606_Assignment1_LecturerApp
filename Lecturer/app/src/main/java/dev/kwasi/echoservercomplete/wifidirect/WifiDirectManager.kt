package dev.kwasi.echoservercomplete.wifidirect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.*
import android.os.Build
import android.util.Log

class WifiDirectManager(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val iFaceImpl: WifiDirectInterface
) : BroadcastReceiver() {

    var groupInfo: WifiP2pGroup? = null

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                iFaceImpl.onWiFiDirectStateChanged(isWifiP2pEnabled)
                Log.d("WFDManager", "WiFi Direct state changed: $isWifiP2pEnabled")
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                    peers?.deviceList?.let { iFaceImpl.onPeerListUpdated(it) }
                    Log.d("WFDManager", "Peer list updated")
                }
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val wifiP2pInfo = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, WifiP2pInfo::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                }

                val tmpGroupInfo = if (wifiP2pInfo?.groupFormed == true) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP, WifiP2pGroup::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
                    }
                } else null

                if (groupInfo != tmpGroupInfo) {
                    groupInfo = tmpGroupInfo
                    iFaceImpl.onGroupStatusChanged(groupInfo)
                    Log.d("WFDManager", "Group status changed")
                }
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val thisDevice = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, WifiP2pDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                }
                iFaceImpl.onDeviceStatusChanged(thisDevice!!)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WFDManager", "Peer discovery initiated successfully")
            }

            override fun onFailure(reason: Int) {
                Log.e("WFDManager", "Failed to initiate peer discovery: $reason")
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun createGroup() {
        manager.createGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WFDManager", "Group created successfully")
            }

            override fun onFailure(reason: Int) {
                Log.e("WFDManager", "Failed to create group: $reason")
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun connectToPeer(peer: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = peer.deviceAddress
        }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WFDManager", "Connected to peer: ${peer.deviceName}")
            }

            override fun onFailure(reason: Int) {
                Log.e("WFDManager", "Failed to connect to peer: ${peer.deviceName}, Reason: $reason")
            }
        })
    }

    fun disconnect() {
        manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WFDManager", "Disconnected successfully")
            }

            override fun onFailure(reason: Int) {
                Log.e("WFDManager", "Failed to disconnect: $reason")
            }
        })
    }
}
