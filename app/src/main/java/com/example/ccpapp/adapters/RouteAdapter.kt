package com.example.ccpapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.models.Route
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RouteAdapter(private val listener: OnRouteClickListener) : 
    RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    interface OnRouteClickListener {
        fun onRouteClicked(route: Route)
    }

    var routes: List<Route> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.bind(route, listener)
    }

    override fun getItemCount(): Int = routes.size

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
        private val tvRouteDate: TextView = itemView.findViewById(R.id.tvRouteDate)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val btnViewMore: Button = itemView.findViewById(R.id.btnViewMore)
        private val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(route: Route, listener: OnRouteClickListener) {
            tvRouteName.text = route.name
            tvRouteDate.text = "Creada el: ${formatDate(route.createdAt)}"
            tvDescription.text = route.description
            tvDueDate.text = "Fecha límite: ${formatDate(route.dueToDate)}"
            
            btnViewMore.setOnClickListener {
                listener.onRouteClicked(route)
            }
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
