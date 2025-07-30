//package com.example.taskmaster
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.taskmaster.databinding.ItemNotificationCardBinding
//
//class NotificationAdapter(
//    private var notifications: List<DeleteRequest>
//) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
//
//    inner class NotificationViewHolder(val binding: ItemNotificationCardBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(notification: DeleteRequest) {
//            binding.notification = notification
//            binding.executePendingBindings()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemNotificationCardBinding.inflate(inflater, parent, false)
//        return NotificationViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
//        holder.bind(notifications[position])
//    }
//
//    override fun getItemCount() = notifications.size
//
//    fun updateData(newNotifications: List<DeleteRequest>) {
//        notifications = newNotifications
//        notifyDataSetChanged()
//    }
//}

package com.example.taskmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.ItemNotificationCardBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val deleteRequests: List<DeleteRequest>,
    private val onItemClick: (DeleteRequest) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding: ItemNotificationCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_notification_card,
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val deleteRequest = deleteRequests[position]
        holder.bind(deleteRequest)
    }

    override fun getItemCount(): Int = deleteRequests.size

    inner class NotificationViewHolder(
        private val binding: ItemNotificationCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(deleteRequest: DeleteRequest) {
            binding.notification = deleteRequest

            // Set the notification title and details
            binding.notificationTitle.text = "Delete Request for '${deleteRequest.taskName}'"

            // Get employee name and format the notification detail
            database.child("employees").child(deleteRequest.requestedById)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val employeeName = snapshot.child("name").getValue(String::class.java) ?: "Unknown Employee"
                        val timeAgo = getTimeAgo(deleteRequest.requestedAt)

                        binding.notificationDetail.text = "$employeeName requested to delete this task • $timeAgo"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        val timeAgo = getTimeAgo(deleteRequest.requestedAt)
                        binding.notificationDetail.text = "Employee requested to delete this task • $timeAgo"
                    }
                })

            // Set click listener
            binding.root.setOnClickListener {
                onItemClick(deleteRequest)
            }

            binding.executePendingBindings()
        }

        private fun getTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60_000 -> "just now"
                diff < 3600_000 -> "${diff / 60_000}m ago"
                diff < 86400_000 -> "${diff / 3600_000}h ago"
                diff < 604800_000 -> "${diff / 86400_000}d ago"
                else -> {
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    dateFormat.format(Date(timestamp))
                }
            }
        }
    }
}
