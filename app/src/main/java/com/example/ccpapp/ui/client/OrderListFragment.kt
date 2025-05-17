package com.example.ccpapp.ui.client

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccpapp.adapters.OrderAdapter
import com.example.ccpapp.databinding.FragmentClientOrderListBinding
import com.example.ccpapp.viewmodels.OrderViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderListFragment : Fragment() {

    private var _binding: FragmentClientOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[OrderViewModel::class.java]
        adapter = OrderAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            orders?.let {
                val sortedOrders = it.sortedByDescending { order ->
                    try {
                        LocalDateTime.parse(order.createdAt, DateTimeFormatter.ISO_DATE_TIME)
                    } catch (e: Exception) {
                        LocalDateTime.MIN
                    }
                }
                adapter.orders = sortedOrders
            }
            binding.progressBar.visibility = View.GONE

            if (orders?.isEmpty() == true) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
        }

        viewModel.loadOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
