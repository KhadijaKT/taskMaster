package com.example.taskmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.ItemEmpTaskCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EmpTaskAdapter(
    private val tasks: List<Task>,
    private val onDeleteRequestClick: (Task) -> Unit
) : RecyclerView.Adapter<EmpTaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemEmpTaskCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemEmpTaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        with(holder.binding) {
            taskName.text = task.title
            taskDetails.text = task.description
            taskAssignedBy.text = "Assigned by: ${task.assignedByName}"

            // Set checkbox state based on status
            taskStatusCheckbox.isChecked = task.status == "Completed"

            // Disable checkbox if already completed
            taskStatusCheckbox.isEnabled = task.status != "Completed"

            // Checkbox listener: mark as completed
            taskStatusCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && task.status != "Completed") {
                    updateTaskStatus(task.id, "Completed")
                }
            }

            // Request delete button
            requestDeleteBtn.setOnClickListener {
                onDeleteRequestClick(task)
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun updateTaskStatus(taskId: String, status: String) {
        val taskRef = FirebaseDatabase.getInstance().reference
            .child("tasks")
            .child(taskId)

        taskRef.child("status").setValue(status)
    }
}
