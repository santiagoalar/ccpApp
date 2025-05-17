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
import com.example.ccpapp.R
import com.example.ccpapp.adapters.WaypointAdapter
import com.example.ccpapp.databinding.FragmentSellerRouteDetailBinding
import com.example.ccpapp.models.Route
import com.example.ccpapp.models.WayPoint
import com.example.ccpapp.viewmodels.RouteViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RouteDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSellerRouteDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var waypointAdapter: WaypointAdapter
    private var googleMap: GoogleMap? = null
    private var selectedRoute: Route? = null

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

        // Inicializar el mapa - Corregido el ID para que coincida con el XML
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragmentId) as SupportMapFragment
        mapFragment.getMapAsync(this)

        routeViewModel.selectedRoute.observe(viewLifecycleOwner) { route ->
            route?.let {
                selectedRoute = it
                updateUI(it)
                // Si el mapa ya está listo, actualizar los marcadores
                googleMap?.let { map ->
                    updateMapWithWaypoints(it.waypoints)
                }
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

        // Actualizar la lista de waypoints
        val sortedWaypoints = route.waypoints.sortedBy { it.order }
        waypointAdapter.waypoints = sortedWaypoints
    }

    private fun updateMapWithWaypoints(waypoints: List<WayPoint>) {
        googleMap?.let { map ->
            map.clear()

            if (waypoints.isEmpty()) return

            val boundsBuilder = LatLngBounds.Builder()
            val polylineOptions = PolylineOptions().width(5f).color(0xFF0000FF.toInt())

            // Agregar marcadores y puntos de la polyline
            waypoints.forEach { waypoint ->
                val position = LatLng(waypoint.latitude, waypoint.longitude)
                map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(waypoint.name)
                        .snippet("Orden: ${waypoint.order}")
                )
                polylineOptions.add(position)
                boundsBuilder.include(position)
            }

            // Agregar la línea que conecta los puntos
            map.addPolyline(polylineOptions)

            // Mover la cámara para que se vean todos los puntos
            try {
                val bounds = boundsBuilder.build()
                val padding = 100 // espacio alrededor de los marcadores en píxeles
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                map.animateCamera(cameraUpdate)
            } catch (e: Exception) {
                // Si solo hay un punto o hay un error con los límites
                if (waypoints.isNotEmpty()) {
                    val firstPoint = LatLng(waypoints.first().latitude, waypoints.first().longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15f))
                }
            }
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Configurar estilo del mapa para modo oscuro si es posible
        try {
            googleMap.setMapStyle(
                com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    com.example.ccpapp.R.raw.map_style_dark
                )
            )
        } catch (e: Exception) {
            // Continuar sin aplicar estilo si hay algún error
        }

        // Si ya tenemos los datos de la ruta, actualizar el mapa
        selectedRoute?.let {
            updateMapWithWaypoints(it.waypoints)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
