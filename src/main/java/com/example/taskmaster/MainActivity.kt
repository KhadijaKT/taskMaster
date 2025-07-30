package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        android.os.Handler().postDelayed({
            startActivity(Intent(this, ActivtyFirstScreen::class.java))
            finish()
        }, 5000)
        FirebaseApp.initializeApp(this)
    }
}