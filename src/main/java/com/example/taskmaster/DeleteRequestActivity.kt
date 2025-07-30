//package com.example.taskmaster
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.taskmaster.databinding.ActivityRequestDeleteBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class DeleteRequestActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRequestDeleteBinding
//    private val deleteRef = FirebaseDatabase.getInstance().getReference("deleteRequests")
//    private val userRef = FirebaseDatabase.getInstance().getReference("employees") // or "employees" based on your structure
//    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRequestDeleteBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val taskId = intent.getStringExtra("taskId") ?: return
//        val taskName = intent.getStringExtra("taskName") ?: return
//
//        binding.taskNameValue.text = taskName
//
//        binding.sendRequestBtn.setOnClickListener {
//            val reason = binding.deleteReasonInput.text.toString().trim()
//
//            if (reason.isEmpty()) {
//                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 🔽 Get employee name from database
//            userRef.child(currentUserId).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val employeeName = snapshot.getValue(String::class.java) ?: "Unknown"
//
//                    val request = DeleteRequest(
//                        taskId = taskId,
//                        taskName = taskName,
//                        reason = reason,
//                        requestedById = currentUserId,
//                        requestedByName = employeeName
//                    )
//
//                    deleteRef.child(taskId).setValue(request)
//                        .addOnSuccessListener {
//                            Toast.makeText(this@DeleteRequestActivity, "Request sent", Toast.LENGTH_SHORT).show()
//                            finish()
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(this@DeleteRequestActivity, "Failed to send request", Toast.LENGTH_SHORT).show()
//                        }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@DeleteRequestActivity, "Could not fetch name", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//}

package com.example.taskmaster

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.databinding.ActivityRequestDeleteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DeleteRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestDeleteBinding
    private val deleteRef = FirebaseDatabase.getInstance().getReference("deleteRequests")
    private val employeesRef = FirebaseDatabase.getInstance().getReference("employees")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getStringExtra("taskId") ?: return
        val taskName = intent.getStringExtra("taskName") ?: return

        binding.taskNameValue.text = taskName

        binding.sendRequestBtn.setOnClickListener {
            val reason = binding.deleteReasonInput.text.toString().trim()

            if (reason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendDeleteRequest(taskId, taskName, reason)
        }
    }

    private fun sendDeleteRequest(taskId: String, taskName: String, reason: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUserEmail = currentUser.email
        if (currentUserEmail == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Find employee by email
        employeesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(employeeSnapshot: DataSnapshot) {
                var employeeId: String? = null
                var employeeName: String? = null

                // Find employee by email
                for (empSnap in employeeSnapshot.children) {
                    val employee = empSnap.getValue(Employee::class.java)
                    if (employee != null && employee.email == currentUserEmail) {
                        employeeId = employee.id
                        employeeName = employee.name
                        break
                    }
                }

                if (employeeId == null || employeeName == null) {
                    Toast.makeText(this@DeleteRequestActivity, "Employee profile not found", Toast.LENGTH_SHORT).show()
                    return
                }

                // Create delete request with correct employee ID and name
                val request = DeleteRequest(
                    taskId = taskId,
                    taskName = taskName,
                    reason = reason,
                    requestedById = employeeId, // Use custom employee ID
                    requestedByName = employeeName // Use employee name from database
                )

                // Save the request
                deleteRef.child(taskId).setValue(request)
                    .addOnSuccessListener {
                        Toast.makeText(this@DeleteRequestActivity, "Request sent successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this@DeleteRequestActivity, "Failed to send request: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeleteRequestActivity, "Failed to load employee data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


