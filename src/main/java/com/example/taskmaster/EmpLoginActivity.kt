package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.databinding.ActivityEmpLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EmpLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("employees")
        auth = FirebaseAuth.getInstance()

        binding.buttonEmpLogin.setOnClickListener {
            val email = binding.editTextEmpEmailOrUsername.text.toString().trim()
            val password = binding.editTextEmpPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginEmployee(email, password)
        }
    }

    private fun loginEmployee(email: String, password: String) {
        // First authenticate with Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { authTask ->
                if (authTask.isSuccessful) {
                    // Firebase Auth successful, now get employee details from database
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        getEmployeeDetails(firebaseUser.uid)
                    }
                } else {
                    // If Firebase Auth fails, try custom database authentication
                    customDatabaseLogin(email, password)
                }
            }
    }

    private fun getEmployeeDetails(userId: String) {
        val email = auth.currentUser?.email ?: return

        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val employee = snapshot.children.first().getValue(Employee::class.java)
                        if (employee != null) {
                            Toast.makeText(this@EmpLoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@EmpLoginActivity, EmployeeDashboardActivity::class.java)
                            intent.putExtra("employeeId", employee.id)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@EmpLoginActivity, "Employee data malformed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EmpLoginActivity, "Employee data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EmpLoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun customDatabaseLogin(email: String, password: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var loginSuccess = false
                var employeeId: String? = null

                for (empSnapshot in snapshot.children) {
                    val employee = empSnapshot.getValue(Employee::class.java)
                    if (employee != null && employee.email == email && employee.password == password) {
                        loginSuccess = true
                        employeeId = employee.id

                        // Create a Firebase Auth user account for this employee
                        createFirebaseAuthUser(email, password, employee)
                        break
                    }
                }

                if (!loginSuccess) {
                    Toast.makeText(this@EmpLoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EmpLoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createFirebaseAuthUser(email: String, password: String, employee: Employee) {
        // Create Firebase Auth account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Update the employee record with the new Firebase Auth UID
                        updateEmployeeWithAuthUID(employee, firebaseUser.uid)
                    }
                } else {
                    // If account already exists, sign in instead
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { signInTask ->
                            if (signInTask.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                if (firebaseUser != null) {
                                    updateEmployeeWithAuthUID(employee, firebaseUser.uid)
                                }
                            } else {
                                Toast.makeText(this@EmpLoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }

    private fun updateEmployeeWithAuthUID(employee: Employee, authUID: String) {
        // Update employee record with Firebase Auth UID if needed
        // For now, just proceed to dashboard
        Toast.makeText(this@EmpLoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@EmpLoginActivity, EmployeeDashboardActivity::class.java)
        intent.putExtra("employeeId", employee.id)
        startActivity(intent)
        finish()

        // Optional: Update tasks to use the new Auth UID
        updateTasksAssignment(employee.id, authUID)
    }

    private fun updateTasksAssignment(oldEmployeeId: String, newAuthUID: String) {
        val tasksRef = FirebaseDatabase.getInstance().getReference("tasks")
        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null && task.assignedTo == oldEmployeeId) {
                        // Update the task to use the new Firebase Auth UID
                        taskSnapshot.ref.child("assignedTo").setValue(newAuthUID)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error silently as this is a background update
            }
        })
    }
}

