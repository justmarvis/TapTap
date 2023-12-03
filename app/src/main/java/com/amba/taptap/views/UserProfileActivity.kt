package com.amba.taptap.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.amba.taptap.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class UserProfileActivity : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference

    private var isFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Get the reference to the database
        val database = FirebaseDatabase.getInstance().reference

        mDbRef = FirebaseDatabase.getInstance().getReference()

        val odaUsernameRef = database.child("")

        val followingButton = findViewById<Button>(R.id.followingButton)

        val username = intent.getStringExtra("username")
        val fullname = intent.getStringExtra("fullname")
        val imageURL = intent.getStringExtra("image")
        val bio = intent.getStringExtra("bio")
        val country = intent.getStringExtra("country")

        // Get the current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if the user is signed in
        if (currentUser != null) {
            val currentUserId = currentUser.uid // Obtain the user ID

            // Get the ID of the user being viewed
            val viewedUserId = "viewed_user_id_here" // Replace with the actual user ID of the profile being viewed

            // Get the reference to the followers list for the viewed user
            val followersRef = FirebaseDatabase.getInstance().getReference("followers").child(viewedUserId)

            // Check if the current user is a follower of the viewed user
            followersRef.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    isFollowing = dataSnapshot.exists() // Assign the value to isFollowing

                    // Now you can use the isFollowing variable to update the button state
                    updateFollowButtonState(followingButton, isFollowing)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                    Log.e("UserProfileActivity", "Error checking follower status: ${databaseError.message}")
                }
            })
        } else {
            // User is not signed in
            // You might want to handle this case based on your app's requirements
        }

        followingButton.setOnClickListener {
            // Toggle follow/unfollow state
            isFollowing = !isFollowing

            // Update button text and perform corresponding action
            updateFollowButtonState(followingButton, isFollowing)

            // Add/remove user from the followers list in Firebase
            updateFollowersList(isFollowing)
        }


        // finish activity
        val regActButton = findViewById<ImageView>(R.id.bckBtn)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            finish()
        }

        // Display the receiving user information
        val usernamePlaceholder = findViewById<TextView>(R.id.username)
        val fullnamePlaceholder = findViewById<TextView>(R.id.fullName)
        val userImagePlaceholder = findViewById<ImageView>(R.id.proImage)
        val bioPlaceholder = findViewById<TextView>(R.id.bio)
        val countryPlaceholder = findViewById<TextView>(R.id.country)

        odaUsernameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve data from the intent


                // Display user bio information
                fullnamePlaceholder.text = fullname ?: "__"
                usernamePlaceholder.text = if (username.isNullOrEmpty()) "__" else "@${username}"
                bioPlaceholder.text = bio ?: "__"
                countryPlaceholder.text = country ?: "__"
                if (!imageURL.isNullOrEmpty()) {
                    Picasso.get()
                        .load(imageURL)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(userImagePlaceholder)
                } else {
                    // Handle the case where imageURL is null or empty
                    userImagePlaceholder.setImageResource(R.drawable.ic_launcher_foreground)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                Log.e("UserProfileActivity", "Error loading image from Firebase: ${databaseError.message}")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateFollowButtonState(button: Button, isFollowing: Boolean) {
        if (isFollowing) {
            button.text = "Unfollow"
            // You may want to customize the button appearance for the "Unfollow" state
            button.setBackgroundResource(R.drawable.custom_button_)
        } else {
            button.text = "Follow"
            // Restore the original button appearance for the "Follow" state
            button.setBackgroundResource(R.drawable.custom_buttons)
        }
    }

    private fun updateFollowersList(isFollowing: Boolean) {
        // Get the current user's ID or other identifier
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Get the reference to the followers list for the current user
        val followersRef = FirebaseDatabase.getInstance().getReference("following")

        // Get the ID of the user being followed
        val followedUserId = "user_id_of_the_user_being_followed" // Replace with the actual user ID

        if (isFollowing) {
            // If the user is following, add the followed user to the followers list
            followersRef.child(followedUserId).setValue(true)
        } else {
            // If the user is unfollowing, remove the followed user from the followers list
            followersRef.child(followedUserId).removeValue()
        }
    }
}