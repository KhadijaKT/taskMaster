package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.databinding.ActivityTasksBinding

class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    companion object {
        private const val TAG = "TasksActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tasks)
        binding.lifecycleOwner = this

        setupRecyclerView()
        setupObservers()
        setupNavigation()

        Log.d(TAG, "TasksActivity created")
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                Log.d(TAG, "Task clicked: ${task.title}")
            },
            onStatusChange = { taskId, newStatus ->
                viewModel.updateTaskStatus(taskId,newStatus)
            },
            onDeleteTask = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )

        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@TasksActivity)
            adapter = taskAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewModel.tasks.observe(this) { tasks ->
            Log.d(TAG, "Received ${tasks.size} tasks")
            taskAdapter.submitList(tasks)
        }

        binding.assignTaskButton.isVisible = true

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Log.e(TAG, "Error: $it")
            }
        }
    }

    private fun setupNavigation() {
        binding.assignTaskButton.setOnClickListener {
            startActivity(Intent(this, AssignTaskActivity::class.java))
        }

        binding.dashboardIcon.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        binding.tasksIcon.setOnClickListener {
            // Already here
        }

        binding.employeesIcon.setOnClickListener {
            startActivity(Intent(this, EmployeesActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTasks() // Force reload tasks on resume
    }
}
