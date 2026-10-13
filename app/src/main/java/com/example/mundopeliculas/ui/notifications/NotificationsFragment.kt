package com.example.mundopeliculas.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mundopeliculas.R

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var adapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvNotifications = view.findViewById<RecyclerView>(R.id.rvNotifications)
        val btnBack = view.findViewById<ImageView>(R.id.btnBackNotifications)
        val tvClearAll = view.findViewById<TextView>(R.id.tvClearAll)
        
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        tvClearAll.setOnClickListener {
            NotificationsManager.clearAll(requireContext())
            refreshList()
            Toast.makeText(requireContext(), getString(R.string.notifications_cleared), Toast.LENGTH_SHORT).show()
        }
        
        setupRecyclerView(rvNotifications)
    }

    private fun setupRecyclerView(rv: RecyclerView) {
        val notifications = NotificationsManager.getNotifications(requireContext())
        
        adapter = NotificationAdapter(notifications.toMutableList()) { notification ->
            NotificationsManager.removeNotification(requireContext(), notification.id)
            refreshList()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    private fun refreshList() {
        val updatedList = NotificationsManager.getNotifications(requireContext())
        adapter.updateList(updatedList)
    }
}