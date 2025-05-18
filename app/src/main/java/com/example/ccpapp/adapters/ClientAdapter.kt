package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.databinding.ItemClientBinding
import com.example.ccpapp.models.Client
import com.example.ccpapp.models.User

class ClientAdapter(
    private val clickListener: OnClientClickListener? = null
) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    interface OnClientClickListener {
        fun onClientClick(client: Client)
        fun onAddDetailsClick(client: Client)
        fun onMakeOrderClick(client: Client)
    }

    var clients: List<Client> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var client: Client? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val withDataBinding: ItemClientBinding = DataBindingUtil.inflate(
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
            clickListener?.onClientClick(clients[position])
        }

        holder.binding.buttonAddDetail.setOnClickListener {
            clickListener?.onAddDetailsClick(clients[position])
        }

        holder.binding.buttonMakeOrder.setOnClickListener {
            clickListener?.onMakeOrderClick(clients[position])
        }
    }

    class ClientViewHolder(val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_client
        }

        @SuppressLint("SetTextI18n")
        fun bind(client: Client) {
            binding.textClientName.text = client.clientName
            binding.textAddress.text = client.address
            binding.textPhone.text = client.clientPhone
            binding.textEmail.text = client.clientEmail
        }
    }

    object UserStorage {
        private val users = mutableListOf<User>()

        fun addUser(user: User) {
            clear()
            users.add(user)
        }

        fun getUsers(): List<User> {
            return users
        }

        fun clear() {
            users.clear()
        }

        fun getZeroUser(): User? {
            return if (users.isNotEmpty()) users[0] else null
        }
    }
}
