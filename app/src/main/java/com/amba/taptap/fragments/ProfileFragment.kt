package com.amba.taptap.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.amba.taptap.R
import com.amba.taptap.views.MessageActivity
import com.amba.taptap.views.PhotoUploaderActivity
import com.amba.taptap.views.ProfileEditActivity
import com.amba.taptap.views.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private var selectedPhotoUri: Uri? = null
    private lateinit var selectPhoto: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize mAuth
        mAuth = FirebaseAuth.getInstance()

        // Get the reference to the database
        mDbRef = FirebaseDatabase.getInstance().reference

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get the reference to the database
        val database = FirebaseDatabase.getInstance().reference

        val regActButton = view.findViewById<Button>(R.id.edtProfile)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivity(intent)
        }

        val uploaderButton = view.findViewById<Button>(R.id.uploadButton)
        uploaderButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            val intent = Intent(requireContext(), PhotoUploaderActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = view.findViewById<ImageView>(R.id.set)
        settingsButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Find the TextView with ID username in your layout
        val usernameTextView = view.findViewById<TextView>(R.id.username)
        val fullnameTextView = view.findViewById<TextView>(R.id.fullName)

        // Read the value of the logged in user
        val userId = mAuth.currentUser?.uid // assuming you have a logged-in user
        if (userId != null) {
            val fullnameRef = mDbRef.child("users").child(userId).child("fullname")
            val usernameRef = mDbRef.child("users").child(userId).child("username")
            fullnameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val fullname = dataSnapshot.getValue(String::class.java)

                    // Update the text of the fullname TextView
                    fullnameTextView.text = fullname ?: "__"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })

            usernameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.getValue(String::class.java)

                    // Update the text of the username TextView
                    usernameTextView.text = if (username.isNullOrEmpty()) "__" else "@${username}"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })
        }

        val proImage = view.findViewById<ImageView>(R.id.proImage)

//        val userId = mAuth.currentUser?.uid // assuming you have a logged-in user
        if (userId != null) {
            val imageRef = mDbRef.child("users").child(userId).child("imageUrl")
            imageRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val imageUrl = dataSnapshot.getValue(String::class.java)

                    // Update the image of the user using the obtained imageUrl
                    if (imageUrl != null) {
                        // Use Picasso to load the image
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.baseline_camera_alt_24)
                            .into(proImage)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })
        }

        // Find the TextView with ID username in your layout
        val bioTextView = view.findViewById<TextView>(R.id.bio)

        // Read the value of the logged in user
        if (userId != null) {
            val bioRef = mDbRef.child("users").child(userId).child("bio")
            bioRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val bio = dataSnapshot.getValue(String::class.java)

                    // Update the text of the username TextView
                    bioTextView.text = bio ?: "__"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })
        }

        // Find the TextView with ID location in your layout
        val locationTextView = view.findViewById<TextView>(R.id.location)

        // Read the value of the logged-in user
        if (userId != null) {
            val countryRef = mDbRef.child("users").child(userId).child("country")
            countryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val countryValue = dataSnapshot.getValue(String::class.java)

                    // Update the text of the location TextView
                    locationTextView.text = countryValue ?: "__"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })
        }





        return view
    }

}