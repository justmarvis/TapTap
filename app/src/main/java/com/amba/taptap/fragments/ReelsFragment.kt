package com.amba.taptap.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amba.taptap.R
import com.amba.taptap.adapter.ChatAdapter
import com.amba.taptap.views.ContactsActivity
import com.amba.taptap.views.MessageActivity
import com.amba.taptap.views.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReelsFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reels, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference.child("latest-chats")
            .child("2jhND9G5hIW54nMw26IHimHgmnNuIu2X0MLk4q0ypfukRgDjCe7ldpF2")

        // Set up RecyclerView for chat messages
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter()
        chatRecyclerView.adapter = chatAdapter

        // Set up ValueEventListener to listen for changes in the "latest-chats" node
        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val message = snapshot.getValue(ChatAdapter.ChatMessage::class.java)
                    if (message != null) {
                        // Add the message to your adapter
                        chatAdapter.addMessage(message)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })

        val regActButton = view.findViewById<ImageView>(R.id.cntct)
        regActButton.setOnClickListener {
            // Create an Intent to open the Contacts activity
            val intent = Intent(requireContext(), ContactsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}