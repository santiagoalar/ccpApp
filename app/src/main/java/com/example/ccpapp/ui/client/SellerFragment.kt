package com.example.ccpapp.ui.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.adapters.ClientAdapter
import com.example.ccpapp.adapters.ProductAdapter
import com.example.ccpapp.databinding.FragmentSellerBinding
import com.example.ccpapp.viewmodels.UserViewModel

class SellerFragment: Fragment() {

    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: UserViewModel
    private var viewModelAdapter: ClientAdapter? = null
    private var navc: NavController?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSellerBinding.inflate(inflater, container, false)
        viewModelAdapter = ClientAdapter()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewClients
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewModelAdapter
        navc = Navigation.findNavController(view)

        viewModel = ViewModelProvider(
            this,
            UserViewModel.Factory(requireActivity().application)
        ).get(UserViewModel::class.java)

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

        viewModel.clients.observe(viewLifecycleOwner) { clients ->
            Log.d("SELLER in FRAG", clients.size.toString())
            clients?.let {
                viewModelAdapter?.clients = it
            }
        }

        // Llama a la función para cargar los datos cuando se crea la vista
        viewModel.refreshClients("SELLER-ID") //TODO cambiar por el user Id del vendedor
    }
}