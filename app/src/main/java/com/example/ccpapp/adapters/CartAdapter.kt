package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.databinding.CartItemBinding
import com.example.ccpapp.models.CartItem

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(cartItem: CartItem, onDeleteClick: (Int) -> Unit, onCartChanged: () -> Unit) {
            binding.textProductName.text = "${cartItem.name} - $${cartItem.unitPrice}"
            binding.textProductPrice.text = "$${cartItem.totalPrice}"
            binding.textProductQuantity.text = "${cartItem.quantity}"
            binding.buttonDecrease.setOnClickListener {
                val current = binding.textProductQuantity.text.toString().toIntOrNull() ?: 0
                if (current > 0) {
                    val newQuantity = current - 1
                    binding.textProductQuantity.text = newQuantity.toString()
                    binding.textProductPrice.text = "$${cartItem.unitPrice * newQuantity}"
                    cartItem.quantity = newQuantity
                    cartItem.totalPrice = cartItem.unitPrice * newQuantity
                    onCartChanged()
                }
            }
            binding.buttonIncrease.setOnClickListener {
                val current = binding.textProductQuantity.text.toString().toIntOrNull() ?: 0
                val maxQuantity = cartItem.maxStock
                if (current < maxQuantity) {
                    val newQuantity = current + 1
                    binding.textProductQuantity.text = newQuantity.toString()
                    binding.textProductPrice.text = "$${cartItem.unitPrice * newQuantity}"
                    cartItem.quantity = newQuantity
                    cartItem.totalPrice = cartItem.unitPrice * newQuantity
                    onCartChanged()
                }
            }
            binding.buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(binding.root.context) {
                        onDeleteClick(position)
                        cartItem.totalPrice = 0
                        cartItem.quantity = 0
                        ProductAdapter.CartStorage.removeItem(cartItem.id)
                        onCartChanged()
                    }
                }

            }
        }

        private fun showDeleteConfirmationDialog(context: Context, onConfirmed: () -> Unit) {
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este producto del carrito?")
                .setPositiveButton("Sí") { dialog, _ ->
                    onConfirmed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val binding = CartItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position], { pos ->
            cartItems.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, cartItems.size)
        }, onCartChanged)
    }

    override fun getItemCount(): Int = cartItems.size
}
