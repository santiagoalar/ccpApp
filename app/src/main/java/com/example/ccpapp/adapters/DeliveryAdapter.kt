package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.databinding.ItemDeliveryBinding
import com.example.ccpapp.models.Delivery
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DeliveryAdapter : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    var deliveries: List<Delivery> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemDeliveryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeliveryViewHolder(binding)
    }

    override fun getItemCount(): Int = deliveries.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(deliveries[position])
    }

    class DeliveryViewHolder(val viewDataBinding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_delivery
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(delivery: Delivery) {
            viewDataBinding.textDeliveryId.text = "Orden #${delivery.orderId}"
            viewDataBinding.textDescription.text = "Descripción: ${delivery.description}"
            viewDataBinding.textEstimatedDate.text = "Fecha estimada: ${formatDate(delivery.estimatedDeliveryDate)}"

            if (delivery.statusUpdates.isEmpty()) {
                viewDataBinding.textNoUpdates.visibility = View.VISIBLE
                viewDataBinding.recyclerViewStatusUpdates.visibility = View.GONE
            } else {
                viewDataBinding.textNoUpdates.visibility = View.GONE
                viewDataBinding.recyclerViewStatusUpdates.visibility = View.VISIBLE
                
                val statusAdapter = StatusUpdateAdapter()
                viewDataBinding.recyclerViewStatusUpdates.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = statusAdapter
                }

                val sortedUpdates = delivery.statusUpdates.sortedByDescending {
                    try {
                        LocalDateTime.parse(it.createdAt, DateTimeFormatter.ISO_DATE_TIME)
                    } catch (e: Exception) {
                        LocalDateTime.MIN
                    }
                }
                
                statusAdapter.statusUpdates = sortedUpdates
            }
        }
        
        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = DateTimeFormatter.ISO_DATE_TIME
                val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateTime = LocalDateTime.parse(dateString, inputFormat)
                dateTime.format(outputFormat)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
