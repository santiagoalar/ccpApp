package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.databinding.ClientItemBinding
import com.example.ccpapp.models.User
import com.example.ccpapp.ui.client.ClientDetailFragment
import com.example.ccpapp.ui.client.SellerFragmentDirections

class ClientAdapter : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    var clients: List<User> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var client: User? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val withDataBinding: ClientItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ClientViewHolder.LAYOUT,
            parent,
            false
        )
        return ClientViewHolder(withDataBinding)
    }

    override fun getItemCount(): Int {
        return clients.size
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.binding.also {
            it.client = clients[position]
        }
        holder.bind(clients[position])
        holder.binding.root.setOnClickListener {
            val action = SellerFragmentDirections.actionSellerFragmentToClientDetailFragment(clients[position].id)
            //Navigate using that action
            holder.binding.root.findNavController().navigate(action)
        }
        holder.binding.buttonAddDetail.setOnClickListener{
            val action = SellerFragmentDirections.actionSellerFragmentToClientDetailFragment(clients[position].id)
            //Navigate using that action
            holder.binding.root.findNavController().navigate(action)
        }
    }

    class ClientViewHolder(val binding: ClientItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.client_item
        }

        @SuppressLint("SetTextI18n")
        fun bind(client: User) {
            binding.textClientName.text = client.name
            binding.textAddress.text = client.name
            binding.textPhone.text = client.phone
            binding.textEmail.text = client.email

            //binding.textProductPrice.text = "$${cartItem.totalPrice}"
            //binding.textProductQuantity.text = "Cantidad: ${cartItem.quantity}"
        }

        /*fun bind(client: User) {
            Glide.with(itemView)
                .load(album.cover.toUri().buildUpon().scheme("https").build())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(viewDataBinding.albumCover)
        }*/

    }
}