package com.example.ccpapp.ui.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccpapp.adapters.VisitRecordAdapter
import com.example.ccpapp.databinding.FragmentSellerVisitRecordsBinding
import com.example.ccpapp.viewmodels.UserViewModel
import com.example.ccpapp.viewmodels.VisitRecordsViewModel

class VisitRecordsFragment : Fragment() {

    private var _binding: FragmentSellerVisitRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var visitViewModel: VisitRecordsViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: VisitRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerVisitRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitViewModel = ViewModelProvider(requireActivity())[VisitRecordsViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        adapter = VisitRecordAdapter()
        binding.visitRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@VisitRecordsFragment.adapter
        }

        userViewModel.refreshClients()

        userViewModel.clientsMap.observe(viewLifecycleOwner) { clientsMap ->
            adapter.updateClientsMap(clientsMap)
        }

        visitViewModel.visitRecords.observe(viewLifecycleOwner) { visits ->
            visits?.let {
                adapter.updateVisits(it)

                if (it.isEmpty()) {
                    binding.visitRecyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.visitRecyclerView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }
            }

            binding.progressBar.visibility = View.GONE
        }

        visitViewModel.loadVisitRecords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
