package dev.kwasi.echoservercomplete.wifidirect

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import dev.kwasi.echoservercomplete.R

class WifiDirectConnectedActivity : AppCompatActivity() {

    private lateinit var tvNetworkInfo: TextView
    private lateinit var btnEndClass: Button
    private lateinit var listViewAttendees: ListView
    private lateinit var tvChatMessages: TextView
    private lateinit var etChatMessage: EditText
    private lateinit var btnSendMessage: Button

    private val attendees = mutableListOf("Student 1", "Student 2", "Student 3")
    private val chatMessages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_connected)

        // Initialize views
        tvNetworkInfo = findViewById(R.id.tvNetworkInfo)
        btnEndClass = findViewById(R.id.btnEndClass)
        listViewAttendees = findViewById(R.id.listViewAttendees)
        tvChatMessages = findViewById(R.id.tvChatMessages)
        etChatMessage = findViewById(R.id.etChatMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        // Show network information
        tvNetworkInfo.text = "Network SSID: MyWiFiDirectNetwork\nPassword: 12345678"

        // End class button listener
        btnEndClass.setOnClickListener {
            // Logic to end the class
            Toast.makeText(this, "Class ended", Toast.LENGTH_SHORT).show()
            finish() // End the activity
        }

        // Set up the attendees list
        val attendeesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, attendees)
        listViewAttendees.adapter = attendeesAdapter

        // Send chat message listener
        btnSendMessage.setOnClickListener {
            val message = etChatMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                chatMessages.add(message)
                updateChatMessages()
                etChatMessage.text.clear() // Clear the input field after sending
            }
        }
    }

    // Update the chat message view
    private fun updateChatMessages() {
        val formattedMessages = chatMessages.joinToString(separator = "\n") { "You: $it" }
        tvChatMessages.text = formattedMessages
    }
}