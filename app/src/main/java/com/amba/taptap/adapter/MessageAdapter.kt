package com.amba.taptap.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amba.taptap.model.Message
import com.amba.taptap.R
import com.amba.taptap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedUser: User? = null

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1) {
            // statement: inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_box_receiver, parent, false)
            return ReceiverViewHolder(view)
        } else {
            // statement: inflate send
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_box, parent, false)
            return SenderViewHolder(view)
        }

    }

//    // Function to set the selected user
    fun setSelectedUser(user: User) {
        selectedUser = user
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder is SenderViewHolder) {
            // Sender view holder
            val viewHolder = holder
            viewHolder.senderMessage.text = currentMessage.message

            // Display timestamp in a TextView
            val timestampTextView = viewHolder.timestampTextView
            val timestampMillis = currentMessage.timestamp
            val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault())
                .format(Date(timestampMillis))
            timestampTextView.text = formattedTimestamp

        } else if (holder is ReceiverViewHolder) {
            // Receiver view holder
            val viewHolder = holder
            viewHolder.receiverMessage.text = currentMessage.message

            // Display timestamp in a TextView
            val timestampTextView = viewHolder.timestampTextView
            val timestampMillis = currentMessage.timestamp
            val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault())
                .format(Date(timestampMillis))
            timestampTextView.text = formattedTimestamp
        }
    }


    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }

    }


    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessage = itemView.findViewById<TextView>(R.id.msg_txt)
        val senderImageView = itemView.findViewById<ImageView>(R.id.userImage)
        val timestampTextView = itemView.findViewById<TextView>(R.id.date)
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMessage = itemView.findViewById<TextView>(R.id.msg_txt)
        val receiverImageView = itemView.findViewById<ImageView>(R.id.userImage)
        val timestampTextView = itemView.findViewById<TextView>(R.id.date)
    }

}