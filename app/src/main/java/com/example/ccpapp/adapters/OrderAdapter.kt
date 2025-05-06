package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.databinding.ItemOrderBinding
import com.example.ccpapp.models.Order
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter() : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var orders: List<Order> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var order: Order? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val withDataBinding: ItemOrderBinding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(withDataBinding)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    class OrderViewHolder(val viewDataBinding: ItemOrderBinding) :

        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_order
        }

        fun bind(order: Order) {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

            viewDataBinding.textOrderId.text = "Orden #${order.id}"
            viewDataBinding.textDate.text = "Fecha: ${formatDate(order.createdAt)}"
            viewDataBinding.textPrice.text =
                "Total: ${format.format(order.total)} ${order.currency}"
            viewDataBinding.textQuantity.text =
                "Cantidad: ${order.quantity} - Estado: ${order.status}"
            viewDataBinding.textSubtotalAndTax.text =
                "Subtotal: ${format.format(order.subtotal)} - Impuestos: ${format.format(order.tax)}"
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = java.time.format.DateTimeFormatter.ISO_DATE_TIME
                val outputFormat = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val dateTime = java.time.LocalDateTime.parse(dateString, inputFormat)
                dateTime.format(outputFormat)
            } catch (e: Exception) {
                dateString
            }
        }
    }


}
