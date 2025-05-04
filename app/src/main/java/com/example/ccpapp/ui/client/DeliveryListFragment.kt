package com.example.ccpapp.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccpapp.adapters.DeliveryAdapter
import com.example.ccpapp.databinding.FragmentListBinding
import com.example.ccpapp.viewmodels.DeliveryViewModel


class DeliveryListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DeliveryViewModel
    private lateinit var adapter: DeliveryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
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
            }
            binding.progressBar.visibility = View.GONE

            if (deliveries?.isEmpty() == true) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
        }

        viewModel.loadDeliveries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
