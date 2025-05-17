package com.example.ccpapp.ui.seller

import android.app.DatePickerDialog
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
import com.example.ccpapp.R
import com.example.ccpapp.adapters.RouteAdapter
import com.example.ccpapp.databinding.FragmentSellerRoutesBinding
import com.example.ccpapp.viewmodels.RouteViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RoutesFragment : Fragment(), RouteAdapter.OnRouteClickListener {

    private var _binding: FragmentSellerRoutesBinding? = null
    private val binding get() = _binding!!
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var adapter: RouteAdapter
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routeViewModel = ViewModelProvider(requireActivity())[RouteViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupDatePicker()

        routeViewModel.loadRoutes()
    }

    private fun setupRecyclerView() {
        adapter = RouteAdapter(this)
        binding.routesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.routesRecyclerView.adapter = adapter
    }
    
    override fun onRouteClicked(route: com.example.ccpapp.models.Route) {
        routeViewModel.setSelectedRoute(route)
        findNavController().navigate(R.id.action_routesFragment_to_routeDetailFragment)
    }

    private fun setupObservers() {
        routeViewModel.routes.observe(viewLifecycleOwner) { routes ->
            binding.progressBar.visibility = View.GONE

            if (routes.isNullOrEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.routesRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.routesRecyclerView.visibility = View.VISIBLE
                adapter.routes = routes
            }
        }

        routeViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date.isNotEmpty()) {
                binding.tvSelectedDate.visibility = View.VISIBLE
                binding.tvSelectedDate.text = "Fecha: $date"
                
                // Cambiar el texto del botón con la fecha formateada
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                try {
                    val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                    parsedDate?.let {
                        binding.btnDatePicker.text = dateFormat.format(it)
                    }
                } catch (e: Exception) {
                    binding.btnDatePicker.text = date
                }
            } else {
                binding.tvSelectedDate.visibility = View.GONE
                // Restaurar el texto original del botón
                binding.btnDatePicker.text = "Seleccionar Fecha"
            }
        }
    }

    private fun setupDatePicker() {
        binding.btnDatePicker.setOnClickListener {
            showDatePickerDialog()
        }
        
        binding.btnClearDate.setOnClickListener {
            clearDateFilter()
        }
    }

    private fun clearDateFilter() {
        routeViewModel.setSelectedDate("")
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)

                // Mostrar la fecha seleccionada y cargar rutas para esa fecha
                routeViewModel.setSelectedDate(formattedDate)
                binding.progressBar.visibility = View.VISIBLE
            },
            year,
            month,
            day
        )

        // Agregar botón para limpiar la fecha
        datePickerDialog.setButton(
            DatePickerDialog.BUTTON_NEUTRAL,
            "Limpiar"
        ) { _, _ ->
            routeViewModel.setSelectedDate("")
            binding.progressBar.visibility = View.VISIBLE
        }

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
