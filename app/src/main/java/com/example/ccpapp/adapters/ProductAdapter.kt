package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ccpapp.R
import com.example.ccpapp.adapters.ProductAdapter.CartStorage.getQuantity
import com.example.ccpapp.databinding.ItemProductBinding
import com.example.ccpapp.models.CartItem
import com.example.ccpapp.models.Product

class ProductAdapter(private val onBuyClick: (Product, Int) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    var products: List<Product> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var product: Product? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val withDataBinding: ItemProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ProductViewHolder.LAYOUT,
            parent,
            false
        )
        return ProductViewHolder(withDataBinding, onBuyClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.product = products[position]
        }
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ProductViewHolder(
        val viewDataBinding: ItemProductBinding,
        private val onBuyClick: (Product, Int) -> Unit
    ) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_product
        }

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            Glide.with(itemView)
                .load(product.images[0].toUri().buildUpon().scheme("https").build())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(viewDataBinding.imageProducto)

            // Modificar esta línea para convertir el número a String
            val quantityText = getQuantity(product.id).toString()
            viewDataBinding.editQuantity.setText(quantityText)

            viewDataBinding.buttonPlus.setOnClickListener {
                val current = viewDataBinding.editQuantity.text.toString().toIntOrNull() ?: 0
                val maxQuantity = product.stock
                if (current < maxQuantity) {
                    viewDataBinding.editQuantity.setText((current + 1).toString())
                }
            }

            viewDataBinding.buttonMinus.setOnClickListener {
                val current = viewDataBinding.editQuantity.text.toString().toIntOrNull() ?: 0
                if (current > 0) {
                    viewDataBinding.editQuantity.setText((current - 1).toString())
                }
            }

            viewDataBinding.buttonBuy.setOnClickListener {
                val quantity = viewDataBinding.editQuantity.text.toString().toIntOrNull() ?: 0
                if (quantity > 0) {
                    onBuyClick(product, quantity)
                    CartStorage.addItem(
                        CartItem(
                            id = product.id,
                            name = product.name,
                            quantity = quantity,
                            unitPrice = product.price,
                            totalPrice = product.price * quantity,
                            maxStock = product.stock
                        )
                    )
                }
            }
        }

    }

    object CartStorage {
        private val items = mutableListOf<CartItem>()
        private var userId: String = ""
        private val total: Int
            get() = items.sumOf { it.totalPrice }

        fun addItem(cartItem: CartItem) {
            val existing = items.find { it.id == cartItem.id }
            if (existing != null) {
                items.remove(existing)
                items.add(existing.copy(quantity = cartItem.quantity))
            } else {
                items.add(cartItem)
            }
        }

        fun getUserId(): String = userId
        fun getItems(): List<CartItem> = items.toList()

        fun clearCart() {
            items.clear()
        }

        fun removeItem(productId: String) {
            items.removeAll { it.id == productId }
        }

        fun getQuantity(productId: String): Int {
            return items.find { it.id == productId }?.quantity ?: 0
        }
    }
}
