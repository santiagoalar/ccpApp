package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.databinding.ItemDeliveryBinding
import com.example.ccpapp.models.Delivery

class DeliveryAdapter() : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    var deliveries: List<Delivery> = emptyList()
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

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(deliveries[position])
    }

    class DeliveryViewHolder(val viewDataBinding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_delivery
        }

        @SuppressLint("SetTextI18n")
        fun bind(delivery: Delivery) {
            viewDataBinding.textDeliveryDate.text = "Delivery #${delivery.arrivalDate}"
            viewDataBinding.textLocation.text = "Date: ${delivery.location}"
            viewDataBinding.textStatus.text = "Status: ${delivery.status}"
            viewDataBinding.textDuration.text = "Duración: ${delivery.duration}"
        }
    }
}