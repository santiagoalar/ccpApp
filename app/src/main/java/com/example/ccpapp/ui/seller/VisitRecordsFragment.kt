package com.example.ccpapp.ui.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccpapp.adapters.VisitRecordAdapter
import com.example.ccpapp.databinding.FragmentVisitRecordsBinding
import com.example.ccpapp.viewmodels.VisitRecordsViewModel

class VisitRecordsFragment : Fragment() {

    private var _binding: FragmentVisitRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: VisitRecordsViewModel
    private lateinit var adapter: VisitRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[VisitRecordsViewModel::class.java]

        adapter = VisitRecordAdapter()
        binding.visitRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@VisitRecordsFragment.adapter
        }

        viewModel.visitRecords.observe(viewLifecycleOwner) { visits ->
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

            // Ocultar el ProgressBar cuando se cargan los datos
            binding.progressBar.visibility = View.GONE
        }

        viewModel.loadVisitRecords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}