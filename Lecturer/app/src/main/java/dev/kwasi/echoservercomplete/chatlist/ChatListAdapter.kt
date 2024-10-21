package dev.kwasi.echoservercomplete.chatlist

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import dev.kwasi.echoservercomplete.R
import dev.kwasi.echoservercomplete.models.ContentModel

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private val chatList: MutableList<ContentModel> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]

        // Adjust message alignment based on sender's IP (Lecturer vs Student)
        (holder.messageTextView.layoutParams as ConstraintLayout.LayoutParams).apply {
            horizontalBias = if (chat.senderIp == "192.168.49.1") 0f else 1f  // Lecturer on left, Student on right
        }
        holder.messageTextView.text = chat.message
    }

    override fun getItemCount(): Int = chatList.size

    fun addItemToEnd(contentModel: ContentModel) {
        chatList.add(contentModel)
        notifyItemInserted(chatList.size - 1)
    }
}
