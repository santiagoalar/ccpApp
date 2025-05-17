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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

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
                
                // Agregar logs para depuración
                Log.d("RouteDetailFragment", "Route waypoints: ${it.waypoints.size}")
                it.waypoints.forEach { waypoint ->
                    Log.d("RouteDetailFragment", "Waypoint: ${waypoint.name}, Lat: ${waypoint.latitude}, Lng: ${waypoint.longitude}")
                }
                
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
        binding.tvDueDate.text = formatDate(route.dueToDate)

        // Actualizar la lista de waypoints
        val sortedWaypoints = route.waypoints.sortedBy { it.order }
        waypointAdapter.waypoints = sortedWaypoints
    }

    private fun updateMapWithWaypoints(waypoints: List<WayPoint>) {
        googleMap?.let { map ->
            map.clear()

            if (waypoints.isEmpty()) {
                Log.d("RouteDetailFragment", "No waypoints to display")
                return
            }

            try {
                val boundsBuilder = LatLngBounds.Builder()
                val polylineOptions = PolylineOptions().width(8f).color(0xFF4CAF50.toInt()) // Color verde más visible

                // Agregar marcadores y puntos de la polyline
                waypoints.forEachIndexed { index, waypoint ->
                    val position = LatLng(waypoint.latitude, waypoint.longitude)
                    
                    // Color diferente para el primer y último marcador
                    val markerOptions = MarkerOptions()
                        .position(position)
                        .title(waypoint.name)
                        .snippet("Orden: ${waypoint.order}")
                    
                    // Primer marcador en verde, último en rojo, resto en azul
                    when (index) {
                        0 -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        waypoints.size - 1 -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        else -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    }
                    
                    map.addMarker(markerOptions)
                    polylineOptions.add(position)
                    boundsBuilder.include(position)
                }

                // Agregar la línea que conecta los puntos
                map.addPolyline(polylineOptions)

                // Mover la cámara para que se vean todos los puntos
                val bounds = boundsBuilder.build()
                val padding = 150 // espacio alrededor de los marcadores en píxeles
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                map.animateCamera(cameraUpdate)
                
                Log.d("RouteDetailFragment", "Map updated with ${waypoints.size} waypoints")
            } catch (e: Exception) {
                Log.e("RouteDetailFragment", "Error updating map: ${e.message}")
                if (waypoints.isNotEmpty()) {
                    val firstPoint = LatLng(waypoints.first().latitude, waypoints.first().longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15f))
                } else {
                    Log.w("RouteDetailFragment", "No waypoints available to center map")
                }
            }
        } ?: Log.e("RouteDetailFragment", "Google Map is null")
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
        Log.d("RouteDetailFragment", "Map is ready")

        // Habilitar controles del mapa para mejor usabilidad
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }

        // Configurar estilo del mapa para modo oscuro si es posible
        try {
            val success = googleMap.setMapStyle(
                com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style_dark
                )
            )
            if (!success) {
                Log.e("RouteDetailFragment", "Style parsing failed")
            }
        } catch (e: Exception) {
            Log.e("RouteDetailFragment", "Can't find style. Error: ", e)
        }

        // Si ya tenemos los datos de la ruta, actualizar el mapa
        selectedRoute?.let {
            Log.d("RouteDetailFragment", "Updating map with ${it.waypoints.size} waypoints from onMapReady")
            updateMapWithWaypoints(it.waypoints)
        } ?: Log.d("RouteDetailFragment", "No selected route available")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
