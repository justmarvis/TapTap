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
import com.amba.taptap.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtUserName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnAgree: Button
    private lateinit var btnReg: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.Email)
        edtUserName = findViewById(R.id.Username)
        edtPassword = findViewById(R.id.Password)
        btnAgree = findViewById(R.id.agreeBtn)
        btnReg = findViewById(R.id.regBtn)

        // add user data to database
        btnReg.setOnClickListener {
            val email = edtEmail.text.toString()
            val username = edtUserName.text.toString()
            val password = edtPassword.text.toString()

            val name = intent.getStringExtra("username")

            register(email, username, password)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val selectPhoto = findViewById<ImageView>(R.id.selectPhoto)
        selectPhoto.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        // create a redirect to registration view
        val regActButton = findViewById<TextView>(R.id.LAccount)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun register(email: String, username: String, password: String) {
        if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User registration successful
                        addUserToDatabase(email, username, password, mAuth.currentUser?.uid!!)
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        uploadImageToFirebaseStorage()
                        finish()
                        startActivity(intent)
                    } else {
                        // User registration failed
                        val exception = task.exception
                        if (exception != null) {
                            Log.e("RegistrationError", exception.message ?: "Unknown error")
                        }
                        Toast.makeText(this@RegisterActivity, "Registration Failed, check all formats and try again!", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Handle the case where email, username, or password is empty
            Toast.makeText(this@RegisterActivity, "Please fill out all fields to register", Toast.LENGTH_SHORT).show()
        }
    }



    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Successfully uploaded image ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(imageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = edtUserName.text.toString() ?: ""

        val user = User(uid,  username, imageUrl )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "Image added to database")
            }
    }

    class User(val uid: String, val username: String, val imageUrl: String)

    private fun addUserToDatabase(email: String, username: String, password: String, uid: String) {
//        val user = User(email, username, password, uid)
        mDbRef = FirebaseDatabase.getInstance().getReference()
//        mDbRef.child("users").child(uid).setValue(user)
    }

    private var selectedPhotoUri: Uri? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected images was
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val selectPhoto = findViewById<ImageView>(R.id.selectPhoto)

            selectPhoto.setImageBitmap(bitmap)

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectPhoto.setBackgroundDrawable(bitmapDrawable)
        }
    }
}