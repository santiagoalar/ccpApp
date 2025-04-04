package com.example.ccpapp.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.ccpapp.R
import com.example.ccpapp.databinding.FragmentSignupBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        val roles = resources.getStringArray(R.array.tipo_usuario)
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item, roles)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.textSignIn.setOnClickListener{ v ->
            val navController = Navigation.findNavController(v)
            navController.navigate(R.id.navigation_home)
        }

        return binding.root
    }
}