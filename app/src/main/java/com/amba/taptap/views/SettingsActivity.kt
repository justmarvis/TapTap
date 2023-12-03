package com.amba.taptap.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.amba.taptap.R
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mAuth = FirebaseAuth.getInstance()


        // create a redirect to registration view
        val regActButton = findViewById<ImageView>(R.id.bckBtn)
        regActButton.setOnClickListener {
            // Create an Intent to open the Registration activity
            finish()
        }

        val logoutButton = findViewById<TextView>(R.id.logout)
        logoutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
            startActivity(intent)
        }
    }
}