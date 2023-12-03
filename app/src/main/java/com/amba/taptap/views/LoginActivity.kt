package com.amba.taptap.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.amba.taptap.MainActivity
import com.amba.taptap.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lateinit var  edtEmail: EditText
        lateinit var  edtPassword: EditText
        lateinit var  btnLog: Button

        lateinit var mAuth: FirebaseAuth

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.Email)
        edtPassword = findViewById(R.id.Password)
        btnLog = findViewById(R.id.logBtn)

        btnLog.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "email and password are required", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // statement: redirect to landing page
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            finish()
                            startActivity(intent)

                        } else {
                            // statement: If login fails, display a message to the user.
                            val exception = task.exception
                            if (exception != null) {
                                Log.e("LoginError", exception.message ?: "Unknown error")
                            }
                            Toast.makeText(this@LoginActivity, "username or password is incorrect", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }

//            login(email, password)
        }
//
//        // create a redirect to main page
//        val bckButton = findViewById<ImageView>(R.id.loginBckBtn)
//        bckButton.setOnClickListener {
//            // Create an Intent to open Chat activity
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

        // create a redirect to registration view
        val regActButton = findViewById<TextView>(R.id.CAccount)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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