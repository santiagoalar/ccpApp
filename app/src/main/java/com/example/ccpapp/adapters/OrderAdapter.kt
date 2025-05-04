package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
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
        /*holder.viewDataBinding.also {
            it.textOrderId = orders[position]
        }*/
        holder.bind(orders[position])
    }

    class OrderViewHolder(val viewDataBinding: ItemOrderBinding) :

        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_order
        }

        fun bind(order: Order) {
            viewDataBinding.textOrderId.text = "Orden #${order.id}"
            viewDataBinding.textDate.text = "Fecha: ${order.date}"
            viewDataBinding.textPrice.text = "Precio: ${order.total}" //TODO modify this for real value
            viewDataBinding.textQuantity.text = "Cantidad de productos: ${order.total}"

            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            //binding.textPrice.text = "Precio: ${format.format(order.price)}"

            //binding.textQuantity.text = "Cantidad de productos: ${order.productCount}"
        }
    }
}
