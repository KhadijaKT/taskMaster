// EmployeeAdapter.kt  ADMIN SIDE
package com.example.taskmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.ItemEmployeeCardBinding

class EmployeeAdapter(
    private val onEmployeeClick: (Employee) -> Unit
) : ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding: ItemEmployeeCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_employee_card,
            parent,
            false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EmployeeViewHolder(
        private val binding: ItemEmployeeCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.employee = employee
            binding.empName.text = employee.name
            binding.empEmail.text = employee.email

            binding.root.setOnClickListener {
                onEmployeeClick(employee)
            }

            binding.executePendingBindings()
        }
    }

    class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}