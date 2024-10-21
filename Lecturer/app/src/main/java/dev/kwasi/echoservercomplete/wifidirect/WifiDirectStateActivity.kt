package dev.kwasi.echoservercomplete.wifidirect

import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.kwasi.echoservercomplete.R

class WifiDirectStateActivity : AppCompatActivity(), WifiDirectInterface {

    private var isAdapterEnabled = false
    private var isGroupFormed = false

    private lateinit var tvAdapterState: TextView
    private lateinit var tvMessage: TextView
    private lateinit var btnEnableWiFiDirect: Button
    private lateinit var btnStartClass: Button

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var wifiDirectManager: WifiDirectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_direct)

        // Initialize views
        tvAdapterState = findViewById(R.id.tvAdapterState)
        tvMessage = findViewById(R.id.tvMessage)
        btnEnableWiFiDirect = findViewById(R.id.btnEnableWiFiDirect)
        btnStartClass = findViewById(R.id.btnStartClass)

        // Set up WiFi Direct Manager
        wifiP2pManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)
        wifiDirectManager = WifiDirectManager(wifiP2pManager, channel, this)

        // Button to turn on WiFi Direct
        btnEnableWiFiDirect.setOnClickListener {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }

        // Button to start class (when WiFi Direct is enabled and no connection exists)
        btnStartClass.setOnClickListener {
            startClassSession()
        }

        // Check initial WiFi Direct state
        updateUI()
    }

    private fun updateUI() {
        if (isAdapterEnabled) {
            tvAdapterState.text = "WiFi Direct State: Enabled"
            tvMessage.text = if (isGroupFormed) {
                "Group is already formed. Ready to start class."
            } else {
                "You are not connected to a WiFi Direct group. You can start a class."
            }
            btnEnableWiFiDirect.visibility = View.GONE
            btnStartClass.visibility = if (!isGroupFormed) View.VISIBLE else View.GONE
        } else {
            tvAdapterState.text = "WiFi Direct State: Disabled"
            tvMessage.text = "Please enable WiFi Direct from settings."
            btnEnableWiFiDirect.visibility = View.VISIBLE
            btnStartClass.visibility = View.GONE
        }
    }

    // Implement WifiDirectInterface
    override fun onWiFiDirectStateChanged(isEnabled: Boolean) {
        isAdapterEnabled = isEnabled
        updateUI()
        Toast.makeText(this, "WiFi Direct state changed: $isEnabled", Toast.LENGTH_SHORT).show()
    }

    override fun onPeerListUpdated(deviceList: Collection<WifiP2pDevice>) {
        // Not needed in this scenario
    }

    override fun onGroupStatusChanged(groupInfo: WifiP2pGroup?) {
        isGroupFormed = groupInfo != null
        updateUI()
    }

    override fun onDeviceStatusChanged(thisDevice: WifiP2pDevice) {
        // Handle device status change if necessary
    }

    private fun startClassSession() {
        // Logic to start class
        Toast.makeText(this, "Class started", Toast.LENGTH_SHORT).show()
        // Move to the next activity or show the class session screen
    }
}