package com.example.ccpapp.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccpapp.adapters.DeliveryAdapter
import com.example.ccpapp.databinding.FragmentClientOrderListBinding
import com.example.ccpapp.viewmodels.DeliveryViewModel


class DeliveryListFragment : Fragment() {

    private var _binding: FragmentClientOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DeliveryViewModel
    private lateinit var adapter: DeliveryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[DeliveryViewModel::class.java]
        adapter = DeliveryAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.deliveries.observe(viewLifecycleOwner) { deliveries ->
            deliveries?.let {
                adapter.deliveries = it
                if (it.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
            binding.progressBar.visibility = View.GONE
        }

        viewModel.loadDeliveries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
