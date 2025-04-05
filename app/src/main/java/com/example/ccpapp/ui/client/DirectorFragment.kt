package com.example.ccpapp.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.ccpapp.databinding.FragmentDirectorBinding
import com.example.ccpapp.viewmodels.UserViewModel

class DirectorFragment: Fragment() {

    private var _binding: FragmentDirectorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private var navc: NavController?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDirectorBinding.inflate(inflater, container, false)

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
}