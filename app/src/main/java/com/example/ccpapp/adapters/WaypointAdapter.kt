package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.databinding.ItemWaypointBinding
import com.example.ccpapp.models.WayPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WaypointAdapter : RecyclerView.Adapter<WaypointAdapter.WaypointViewHolder>() {

    var waypoints: List<WayPoint> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaypointViewHolder {
        val binding = ItemWaypointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WaypointViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WaypointViewHolder, position: Int) {
        val waypoint = waypoints[position]
        holder.bind(waypoint)
    }

    override fun getItemCount(): Int = waypoints.size

    class WaypointViewHolder(private val binding: ItemWaypointBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(waypoint: WayPoint) {
            binding.tvWaypointName.text = waypoint.name
            binding.tvWaypointOrder.text = "Orden: ${waypoint.order}"
            binding.tvCreationDate.text = "Fecha de creación: ${formatDate(waypoint.createdAt)}"
            binding.tvAddress.text = "Dirección: ${waypoint.address}"
            binding.tvCoordinates.text = "Coordenadas: ${waypoint.latitude}, ${waypoint.longitude}"
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateTime = LocalDateTime.parse(dateString, inputFormatter)
                dateTime.format(outputFormatter)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
