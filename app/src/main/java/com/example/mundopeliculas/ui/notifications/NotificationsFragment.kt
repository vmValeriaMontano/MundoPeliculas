package com.example.mundopeliculas.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mundopeliculas.MainActivity
import com.example.mundopeliculas.R

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var adapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvNotifications = view.findViewById<RecyclerView>(R.id.rvNotifications)
        val btnBack = view.findViewById<ImageView>(R.id.btnBackNotifications)
        val tvClearAll = view.findViewById<TextView>(R.id.tvClearAll)

        btnBack.setOnClickListener {
            // Si hay fragmentos en la pila, retrocede; si no, regresa al Home de MainActivity
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    putExtra("TARGET_FRAGMENT", "HOME")
                }
                startActivity(intent)
                requireActivity().finish()
            }
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