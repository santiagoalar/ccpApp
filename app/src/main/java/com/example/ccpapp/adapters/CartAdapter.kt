package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.databinding.CartItemBinding
import com.example.ccpapp.models.CartItem

class CartAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(cartItem: CartItem) {
            binding.textProductName.text = cartItem.name
            binding.textProductPrice.text = "$${cartItem.totalPrice}"
            binding.textProductQuantity.text = "Cantidad: ${cartItem.quantity}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size
}
