package com.example.ccpapp.ui.seller

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.adapters.WaypointAdapter
import com.example.ccpapp.databinding.FragmentSellerRouteDetailBinding
import com.example.ccpapp.models.Route
import com.example.ccpapp.viewmodels.RouteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RouteDetailFragment : Fragment() {
    
    private var _binding: FragmentSellerRouteDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var waypointAdapter: WaypointAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerRouteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
   
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routeViewModel = ViewModelProvider(requireActivity())[RouteViewModel::class.java]

        recyclerView = binding.rvWaypoints
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        waypointAdapter = WaypointAdapter()
        recyclerView.adapter = waypointAdapter

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        routeViewModel.selectedRoute.observe(viewLifecycleOwner) { route ->
            route?.let {
                updateUI(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(route: Route) {
        binding.tvRouteName.text = route.name
        binding.tvRouteDescription.text = route.description
        binding.tvRouteZone.text = "Zona: ${route.zone}"
        binding.tvRouteCreatedAt.text = "Creada el: ${formatDate(route.createdAt)}"
        binding.tvRouteUpdatedAt.text = "Actualizada el: ${formatDate(route.updatedAt)}"
        binding.tvRouteDueDate.text = "Fecha límite: ${formatDate(route.dueToDate)}"
        binding.tvRouteUserId.text = "ID de usuario: ${route.userId}"
        
        // Actualizar la lista de waypoints
        val sortedWaypoints = route.waypoints.sortedBy { it.order }
        waypointAdapter.waypoints = sortedWaypoints
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
