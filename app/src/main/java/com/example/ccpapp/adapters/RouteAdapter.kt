package com.example.ccpapp.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.databinding.ItemRouteBinding
import com.example.ccpapp.models.Route
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RouteAdapter(private val clickListener: OnRouteClickListener) :
    RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    interface OnRouteClickListener {
        fun onViewMoreClicked(route: Route)
    }

    var routes: List<Route> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RouteViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RouteViewHolder, position: Int): Unit {
        val route = routes[position]
        holder.bind(route)

        holder.viewDataBinding.btnViewMore.setOnClickListener {
            clickListener.onViewMoreClicked(route)
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    fun updateRoutes(newRoutes: List<Route>) {
        routes = newRoutes
        notifyDataSetChanged()
    }

    class RouteViewHolder(val viewDataBinding: ItemRouteBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_route
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(route: Route) {
            viewDataBinding.tvRouteName.text = route.name
            viewDataBinding.tvRouteDate.text = "Creada el: ${formatDate(route.createdAt)}"
            viewDataBinding.tvDescription.text = "Descripción: ${route.description}"
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateTime = LocalDateTime.parse(dateString, inputFormatter)
                dateTime.format(outputFormatter)
            } catch (e: Exception) {
                dateString
            }
        }

    }
}
