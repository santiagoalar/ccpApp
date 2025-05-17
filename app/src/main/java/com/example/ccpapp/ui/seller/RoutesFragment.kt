package com.example.ccpapp.ui.seller

import androidx.fragment.app.Fragment
import com.example.ccpapp.adapters.RouteAdapter
import com.example.ccpapp.databinding.FragmentSellerRoutesBinding
import com.example.ccpapp.viewmodels.RouteViewModel

class RoutesFragment : Fragment() {

    private var _binding: FragmentSellerRoutesBinding? = null
    private val binding get() = _binding!!
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var adapter: RouteAdapter
}