package com.amba.taptap.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amba.taptap.R
import com.amba.taptap.adapter.MessageAdapter
import com.amba.taptap.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MessageActivity : AppCompatActivity() {

    private lateinit var  chatRecyclerView: RecyclerView
    private lateinit var  type: EditText
    private lateinit var  sndButton: ImageView
    private lateinit var  messageAdapter: MessageAdapter
    private lateinit var  messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        // create a redirect to registration view
        val regActButton = findViewById<ImageView>(R.id.bckBtn)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            finish()
        }

        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        simulateNewMessage()

        // Get the reference to the database
        val database = FirebaseDatabase.getInstance().reference

        // Get the reference to the user's username
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val usernameRef = database.child("users").child(currentUserId ?: "").child("username")
        val odaUsernameRef = database.child("")

        //        val usernameRef = currentUserId?.let { database.child("users").child(it).child("username") }


        val username = intent.getStringExtra("username")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

//        supportActionBar?.title = username


        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        type = findViewById(R.id.type)
        sndButton = findViewById(R.id.sndButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter( this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter



        //add message to recyclerView
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear() // this allows the chat recycler view to display all data in database
                    for(postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }
                    messageAdapter.notifyDataSetChanged()

                    chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



        // Add message to database with timestamp
        sndButton.setOnClickListener {
            val message = type.text.toString()
//            val timestamp = ServerValue.TIMESTAMP  // Firebase server timestamp

            // Use HashMap to represent the timestamp
            val timestamp: MutableMap<String, Any> = HashMap()
            timestamp["timestamp"] = ServerValue.TIMESTAMP

            val messageObject = Message(message, senderUid, System.currentTimeMillis())

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject)
                .addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

            type.setText("")

            val lastestChatRef = FirebaseDatabase.getInstance().getReference("/lastest-chats/$senderRoom/$receiverRoom")
            lastestChatRef.setValue(message)

            val lastestChatToRef = FirebaseDatabase.getInstance().getReference("/lastest-chats/$receiverRoom/$senderRoom")
            lastestChatToRef.setValue(message)
        }


        // Read the value of the receiving user
        odaUsernameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Retrieve data from the intent
                val username = intent.getStringExtra("username")
                val fullname = intent.getStringExtra("fullname")
                val imageURL = intent.getStringExtra("image")
                val uid = intent.getStringExtra("uid")
                val bio = intent.getStringExtra("bio")
                val country = intent.getStringExtra("country")

                // Display the receiving username on the action
                val usernamePlaceholder = findViewById<TextView>(R.id.userName)
                val userImagePlaceholder = findViewById<ImageView>(R.id.userImage)
                usernamePlaceholder.text = if (username.isNullOrEmpty()) "__" else "@${username}"

                // Load and display the user image using Picasso
                if (!imageURL.isNullOrEmpty()) {
                    Picasso.get()
                        .load(imageURL)
                        .placeholder(R.drawable.ic_launcher_foreground) // Placeholder for loading image
                        .error(R.drawable.ic_launcher_foreground) // Error image if loading fails
                        .into(userImagePlaceholder)
                } else {
                    // Handle the case where imageURL is null or empty
                    userImagePlaceholder.setImageResource(R.drawable.ic_launcher_foreground)
                }

                // Set an OnClickListener to navigate to UserProfileActivity when the username is clicked
                usernamePlaceholder.setOnClickListener {
                    usernamePlaceholder.setTextColor(ContextCompat.getColorStateList(this@MessageActivity, R.color.text_color_selector))
                    val intent = Intent(this@MessageActivity, UserProfileActivity::class.java)

                    // Pass the necessary information to the UserProfileActivity
                    intent.putExtra("fullname", fullname)
                    intent.putExtra("username", username)
                    intent.putExtra("uid", uid)
                    intent.putExtra("image", imageURL)
                    intent.putExtra("bio", bio)
                    intent.putExtra("country", country)

                    startActivity(intent)
                }


                // Set an OnClickListener to navigate to UserProfileActivity when the profile image is clicked
                userImagePlaceholder.setOnClickListener {
                    val intent = Intent(this@MessageActivity, UserProfileActivity::class.java)

                    // Pass the necessary information to the UserProfileActivity
                    intent.putExtra("fullname", fullname)
                    intent.putExtra("username", username)
                    intent.putExtra("uid", uid)
                    intent.putExtra("image", imageURL)
                    intent.putExtra("bio", bio)
                    intent.putExtra("country", country)

                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
    }

    private fun simulateNewMessage() {
        // Simulate receiving a new message
        val newMessage = "Hello there! This is a new message."

        zoomInLatestMessage()
    }

    private fun zoomInLatestMessage() {
        chatRecyclerView.apply {
            scaleX = 0f
            scaleY = 0f
            visibility = View.VISIBLE

            // Use ViewPropertyAnimator for the zoom effect
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(150) // Adjust the duration as needed
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                onBackPressed()
                return true
            }
            // Add other menu items handling if needed
        }
        return super.onOptionsItemSelected(item)
    }
}