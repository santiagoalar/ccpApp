package com.example.ccpapp.ui.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ccpapp.R
import com.example.ccpapp.databinding.FragmentSignupBinding
import com.example.ccpapp.viewmodels.UserViewModel
import org.json.JSONObject

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private var navc: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        val roles = resources.getStringArray(R.array.tipo_usuario)
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item, roles)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.textSignIn.setOnClickListener { v ->
            val navController = Navigation.findNavController(v)
            navController.navigate(R.id.navigation_home)
        }

        binding.btnCreateUser.setOnClickListener {
            val name = binding.inputNombres.text.toString()
            val email = binding.inputCorreo.text.toString()
            val phone = binding.inputCelular.text.toString()
            val password = binding.inputPassword.text.toString()
            val password2 = binding.inputRePassword.text.toString()
            val role = binding.autoCompleteTextView.text.toString()

            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || password2.isBlank() || role.isBlank()) {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Correo no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != password2) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val jsonBody = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("phone", phone)
                put("password", password)
                put("role", role)
            }

            viewModel.postUser(jsonBody)
        }

        return binding.root
    }

    private fun observePostUserResult(view: View) {
        viewModel.postUserResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                val navController = Navigation.findNavController(view)
                navController.navigate(R.id.navigation_home)
            } else {
                Toast.makeText(requireContext(), "Usuario ya existente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        viewModel = ViewModelProvider(this, UserViewModel.Factory(activity.application))[UserViewModel::class.java]

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        viewModel = ViewModelProvider(
            this,
            UserViewModel.Factory(activity.application)
        )[UserViewModel::class.java]

        navc = Navigation.findNavController(view)
        observePostUserResult(view)

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }
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
