//package com.example.taskmaster
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.taskmaster.databinding.ItemTaskCardBinding
//
//class TaskAdapter(
//    private val onTaskClick: (Task) -> Unit,
//    private val onStatusChange: (String, String) -> Unit,
//    private val onDeleteTask: (String) -> Unit
//) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
//
//    inner class TaskViewHolder(val binding: ItemTaskCardBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(task: Task) {
//            binding.task = task
//            binding.executePendingBindings()
//
//            binding.root.setOnClickListener { onTaskClick(task) }
//
//            // Make the status text clickable for changing status
//            binding.taskStatus.setOnClickListener {
//                val newStatus = when (task.status) {
//                    "Pending" -> "In Progress"
//                    "In Progress" -> "Completed"
//                    else -> "Pending"
//                }
//                onStatusChange(task.id, newStatus)
//            }
//
//            // Add delete functionality (if needed)
//            binding.root.setOnLongClickListener {
//                onDeleteTask(task.id)
//                true
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
//        val binding = ItemTaskCardBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return TaskViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
//        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
//        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
//    }
//}
//
//

package com.example.taskmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.ItemTaskCardBinding

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onStatusChange: (String, String) -> Unit,
    private val onDeleteTask: (String) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskCardBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            // Bind task data to views
            binding.taskName.text = task.title
            binding.taskDetails.text = task.description
            binding.taskStatus.text = task.status
            binding.taskAssignedTo.text = "Assigned to: ${task.assignedToName}"

            // Click on card
            binding.root.setOnClickListener { onTaskClick(task) }

            // Delete button
            binding.btnDeleteTask.setOnClickListener {
                onDeleteTask(task.id)
            }

            // Optional: Toggle status (e.g., change between "Pending" and "Completed")
            binding.taskStatus.setOnClickListener {
                val newStatus = if (task.status == "Pending") "Completed" else "Pending"
                onStatusChange(task.id, newStatus)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}


