package com.example.ccpapp.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ccpapp.databinding.FragmentListBinding


class DeliveryListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    //private lateinit var viewModel: OrderViewModel
    //private lateinit var adapter: DeliveryAdapter

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

        /*viewModel = ViewModelProvider(requireActivity())[OrderViewModel::class.java]
        adapter = DeliveryAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.deliveries.observe(viewLifecycleOwner) { deliveries ->
            adapter.setDeliveries(deliveries)
            binding.progressBar.visibility = View.GONE

            if (deliveries.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
        }

        viewModel.loadDeliveries()*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
