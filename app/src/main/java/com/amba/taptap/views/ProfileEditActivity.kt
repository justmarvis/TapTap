package com.amba.taptap.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.amba.taptap.MainActivity
import com.amba.taptap.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtFullName: EditText
    private lateinit var edtUserName: EditText
    private lateinit var edtBio: EditText
    private lateinit var edtCountry: EditText
    private lateinit var btnSave: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private var currentUser: User? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference


        // Initialize your UI elements
        edtEmail = findViewById(R.id.email)
        edtUserName = findViewById(R.id.username)
        edtFullName = findViewById(R.id.fullName)
        edtBio = findViewById(R.id.bio)
        edtCountry = findViewById(R.id.country)
        btnSave = findViewById(R.id.save)


        val proImage = findViewById<ImageView>(R.id.proImage)
        val fullnamePlaceholder = findViewById<TextView>(R.id.fullName)
        val usernamePlaceholder = findViewById<TextView>(R.id.username)
        val bioTextView = findViewById<TextView>(R.id.bio)
        val locationTextView = findViewById<TextView>(R.id.country)
        val userId = mAuth.currentUser?.uid

//        val userId = mAuth.currentUser?.uid // assuming you have a logged-in user
        if (userId != null) {
            val usernameRef = mDbRef.child("users").child(userId).child("imageUrl")
            usernameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val imageUrl = dataSnapshot.getValue(String::class.java)

                    // Update the image of the user using the obtained imageUrl
                    // Assuming you have an ImageView named proImage in your fragment layout
                    if (imageUrl != null) {
                        // Use Picasso to load the image
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_foreground) // You can set a placeholder image
                            .error(R.drawable.baseline_camera_alt_24) // You can set an error image
                            .into(proImage) // Correct ID of your ImageView
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })
        }

        // Read the value of the logged in user and display in UI
        if (userId != null) {
            val fullnameRef = mDbRef.child("users").child(userId).child("fullname")
            val usernameRef = mDbRef.child("users").child(userId).child("username")
            val bioRef = mDbRef.child("users").child(userId).child("bio")
            val countryRef = mDbRef.child("users").child(userId).child("country")

            fullnameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val fullname = dataSnapshot.getValue(String::class.java)

                    // Update the text of the fullname TextView
                    fullnamePlaceholder.text = fullname ?: "__"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })

            usernameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.getValue(String::class.java)

                    // Update the text of the username TextView
                    usernamePlaceholder.text = username ?: "__"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                }
            })

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

        btnSave.setOnClickListener {
            // Retrieve user input
            val email = edtEmail.text.toString()
            val username = edtUserName.text.toString()
            val fullname = edtFullName.text.toString()
            val bio = edtBio.text.toString()
            val country = edtCountry.text.toString()

            // Pass the data to the method for uploading to Firebase Storage and Database
            uploadDataToFirebase(email, username, fullname, bio, country)

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        val selectPhoto = findViewById<ImageView>(R.id.proImage)
        selectPhoto.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        // create a redirect to registration view
        val regActButton = findViewById<ImageView>(R.id.bckBtn)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            finish()
        }

//        // create a redirect to registration view
//        val saveButton = findViewById<Button>(R.id.save)
//        saveButton.setOnClickListener {
//            // Create an Intent to open Chat activity
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun uploadDataToFirebase(email: String, username: String, fullname: String, bio: String, country: String) {
        // Ensure the selectedPhotoUri is not null before proceeding
        if (selectedPhotoUri == null) {
            // Don't show any message or handle this case silently
            return
        }

        // Generate a unique filename for the image
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/images/$filename")

        // Upload the image to Firebase Storage
        storageRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { uploadTask ->
                // Get the download URL of the uploaded image
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Fetch the existing user data from Firebase Database
                    fetchCurrentUserFromDatabase(email, username, fullname, bio, country, uri.toString())
                }.addOnFailureListener {
                    // Handle failure to get download URL
                    Log.e("ProfileEditActivity", "Failed to get download URL: ${it.message}")
                }
            }
            .addOnFailureListener {
                // Handle failure to upload image to Firebase Storage
                Log.e("ProfileEditActivity", "Failed to upload image: ${it.message}")
            }
    }


    private fun fetchCurrentUserFromDatabase(
        email: String,
        username: String,
        fullname: String,
        bio: String,
        country: String,
        imageUrl: String
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val databaseRef = FirebaseDatabase.getInstance().getReference("/users/$uid")

        // Fetch the current user data
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)

                // Use the fetched data to update the user in the database
                currentUser?.let { user ->
                    updateUserDataInDatabase(email, username, fullname, bio, country, imageUrl, user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e("ProfileEditActivity", "Database error: ${error.message}")
            }
        })
    }


    class User(
        var uid: String = "",
        var username: String = "",
        var imageUrl: String = "",
        var bio: String = "",
        var fullname: String = "",
        var email: String = "",
        var country: String
    ) {
        // No-argument constructor required by Firebase
        constructor() : this("", "", "", "", "", "", "")
    }

    private fun updateUserDataInDatabase(
        email: String,
        username: String,
        fullname: String,
        bio: String,
        country: String,
        imageUrl: String,
        currentUser: User
    ) {
        // Update only the fields that have changed
        if (email.isNotEmpty() && email != currentUser.email) {
            currentUser.email = email
        }

        if (username.isNotEmpty() && username != currentUser.username) {
            currentUser.username = username
        }

        if (fullname.isNotEmpty() && fullname != currentUser.fullname) {
            currentUser.fullname = fullname
        }

        if (bio.isNotEmpty() && bio != currentUser.bio) {
            currentUser.bio = bio
        }

        if (country.isNotEmpty() && country != currentUser.country) {
            currentUser.country = country
        }

        if (imageUrl.isNotEmpty() && imageUrl != currentUser.imageUrl) {
            currentUser.imageUrl = imageUrl
        }

        // Save the updated User object to Firebase Database
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val databaseRef = FirebaseDatabase.getInstance().getReference("/users/$uid")

        databaseRef.setValue(currentUser)
            .addOnSuccessListener {
                // Handle success
                Log.d("ProfileEditActivity", "User data updated successfully")
            }
            .addOnFailureListener {
                // Handle failure
                Log.e("ProfileEditActivity", "Failed to update user data: ${it.message}")
            }
    }

    private var selectedPhotoUri: Uri? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected images was
            Log.d("ProfileEditActivity", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val selectPhoto = findViewById<ImageView>(R.id.proImage)

            selectPhoto.setImageBitmap(bitmap)

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectPhoto.setBackgroundDrawable(bitmapDrawable)
        }
    }
}