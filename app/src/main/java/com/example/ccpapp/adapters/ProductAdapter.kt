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
import com.example.ccpapp.databinding.ProductItemBinding
import com.example.ccpapp.models.Product

class ProductAdapter(private val onBuyClick: (Product, Int) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

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
        val withDataBinding: ProductItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ProductViewHolder.LAYOUT,
            parent,
            false
        )
        return ProductViewHolder(withDataBinding, onBuyClick)
    }

    override fun onBindViewHolder(holder:ProductViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.product = products[position]
        }
        holder.bind(products[position])
        /*holder.viewDataBinding.root.setOnClickListener {
            val action =
                AlbumFragmentDirections.actionAlbumFragmentToTrackFragment(albums[position].albumId)
            // Navigate using that action
            holder.viewDataBinding.root.findNavController().navigate(action)
        }*/
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ProductViewHolder(val viewDataBinding: ProductItemBinding,
                            private val onBuyClick: (Product, Int) -> Unit) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.product_item
        }

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            Glide.with(itemView)
                .load(product.imageUrl.toUri().buildUpon().scheme("https").build())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(viewDataBinding.imageProducto)

            viewDataBinding.editQuantity.setText("0")

            viewDataBinding.buttonPlus.setOnClickListener {
                val current = viewDataBinding.editQuantity.text.toString().toIntOrNull() ?: 0
                val maxQuantity = product.quantity ?: 0
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
                    CartStorage.addItem(product)
                    //binding.cartBadge.visibility = View.VISIBLE
                    //viewDataBinding.cart
                }
            }
        }

    }

    object CartStorage {
        //data class CartItem(val productId: String, val quantity: Int, val price: Int)

        private val items = mutableListOf<Product>()

        fun addItem(product: Product) {
            val existing = items.find { it.id == product.id }
            if (existing != null) {
                // Si ya existe, actualiza la cantidad
                items.remove(existing)
                items.add(existing.copy(quantity = existing.quantity + 1000)) //TODO Ojo que era una actualización pero no se de donde viene
            } else {
                items.add(product)
            }
        }

        fun getItems(): List<Product> = items.toList()

        fun clearCart() {
            items.clear()
        }
    }
}