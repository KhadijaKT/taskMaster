package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.databinding.ActivityFirstScreenBinding

class ActivtyFirstScreen : AppCompatActivity() {

    private lateinit var binding: ActivityFirstScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAdmin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.buttonEmployee.setOnClickListener {
            startActivity(Intent(this, EmpLoginActivity::class.java))
        }
    }
}
