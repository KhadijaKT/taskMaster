package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.taskmaster.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity:AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_signup)
        auth=FirebaseAuth.getInstance()

        binding.textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.buttonSignup.setOnClickListener{
            val name= binding.editTextName.text.toString()
            val email=binding.editTextEmail.text.toString()
            val password=binding.editTextPassword.text.toString()

            if(name.isEmpty()||email.isEmpty()||password.isEmpty()){
                Toast.makeText(this,"Please fill in all feilds",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task->
                    if(task.isSuccessful){
                        val uid=auth.currentUser?.uid
                        val userMap=mapOf("name" to name,"email" to email)

                        FirebaseDatabase.getInstance().getReference("users").child(uid!!)
                            .setValue(userMap)
                            .addOnCompleteListener{
                                if(it.isSuccessful){
                                    Toast.makeText(this,"Signup Successful",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this,AdminDashboardActivity::class.java))
                                    finish()
                                }
                                else{
                                    Toast.makeText(this,"Database Error: ${it.exception?.message}",Toast.LENGTH_SHORT)
                                }
                            }
                    }
                    else{
                        Toast.makeText(this,"Signup Failed: ${task.exception?.message}",Toast.LENGTH_SHORT)
                    }
                }
        }
    }
}