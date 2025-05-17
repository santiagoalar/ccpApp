package com.example.ccpapp.ui.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccpapp.R
import com.example.ccpapp.adapters.RouteAdapter
import com.example.ccpapp.databinding.FragmentSellerRoutesBinding
import com.example.ccpapp.models.Route
import com.example.ccpapp.viewmodels.RouteViewModel

class RoutesFragment : Fragment(), RouteAdapter.OnRouteClickListener {

    private var _binding: FragmentSellerRoutesBinding? = null
    private val binding get() = _binding!!
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var adapter: RouteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routeViewModel = ViewModelProvider(requireActivity())[RouteViewModel::class.java]

        adapter = RouteAdapter(this)

        binding.routesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.routesRecyclerView.adapter = adapter

        routeViewModel.routes.observe(viewLifecycleOwner) { routes ->
            routes?.let {
                adapter.routes = it
                if (it.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.routesRecyclerView.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.routesRecyclerView.visibility = View.VISIBLE
                }
            }
            binding.progressBar.visibility = View.GONE
        }

        routeViewModel.loadRoutes()
    }

    override fun onViewMoreClicked(route: Route) {
        // Guardar la ruta seleccionada en el ViewModel
        routeViewModel.setSelectedRoute(route)

        // Navegar al fragmento de detalle de ruta
        findNavController().navigate(R.id.action_routesFragment_to_routeDetailFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
