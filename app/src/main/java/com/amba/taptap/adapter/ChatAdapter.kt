package com.amba.taptap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amba.taptap.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages: MutableList<ChatMessage> = mutableListOf()

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userImage: CircleImageView = itemView.findViewById(R.id.userImage)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val recentChat: TextView = itemView.findViewById(R.id.recentChat)

        fun bind(message: ChatMessage) {
            // Bind data to views
            userName.text = message.sender
            recentChat.text = message.message

//            // Load user image using Picasso
//            Picasso.get()
//                .load(message.receivingUser.imageUrl) // Assuming the receivingUser has an imageUrl property
//                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image while loading
//                .error(R.drawable.ic_launcher_foreground) // Error image if loading fails
//                .into(userImage)
        }
    }

    data class ChatMessage(val sender: String, val message: String)
}
