package com.example.mundopeliculas.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mundopeliculas.R

class NotificationAdapter(
    private var notifications: MutableList<AppNotification>,
    private val onDeleteClick: (AppNotification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivNotificationIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvNotificationTitle)
        val tvMessage: TextView = view.findViewById(R.id.tvNotificationMessage)
        val tvTime: TextView = view.findViewById(R.id.tvNotificationTime)
        val ivDelete: ImageView = view.findViewById(R.id.ivDeleteNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.tvTitle.text = notification.title
        holder.tvMessage.text = notification.message
        holder.tvTime.text = notification.time

        // Icono según tipo
        when (notification.type) {
            "new_movie" -> holder.ivIcon.setImageResource(android.R.drawable.ic_menu_slideshow)
            "reminder" -> holder.ivIcon.setImageResource(android.R.drawable.ic_popup_reminder)
            else -> holder.ivIcon.setImageResource(android.R.drawable.ic_dialog_info)
        }

        holder.ivDelete.setOnClickListener {
            onDeleteClick(notification)
        }
    }

    override fun getItemCount() = notifications.size

    fun updateList(newList: List<AppNotification>) {
        notifications = newList.toMutableList()
        notifyDataSetChanged()
    }
}