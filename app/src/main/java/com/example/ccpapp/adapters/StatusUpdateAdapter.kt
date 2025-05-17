package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.databinding.ItemStatusUpdateBinding
import com.example.ccpapp.models.StatusUpdates
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatusUpdateAdapter : RecyclerView.Adapter<StatusUpdateAdapter.StatusUpdateViewHolder>() {

    var statusUpdates: List<StatusUpdates> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusUpdateViewHolder {
        val binding = ItemStatusUpdateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatusUpdateViewHolder(binding)
    }

    override fun getItemCount(): Int = statusUpdates.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StatusUpdateViewHolder, position: Int) {
        holder.bind(statusUpdates[position])
    }

    class StatusUpdateViewHolder(private val binding: ItemStatusUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(statusUpdate: StatusUpdates) {
            binding.textStatusDate.text = formatDate(statusUpdate.createdAt)
            binding.textStatusName.text = statusUpdate.status
            binding.textStatusDescription.text = statusUpdate.description
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = DateTimeFormatter.ISO_DATE_TIME
                val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val dateTime = LocalDateTime.parse(dateString, inputFormat)
                dateTime.format(outputFormat)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
