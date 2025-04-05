package com.example.ccpapp.ui.home

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
import androidx.navigation.Navigation.findNavController
import com.example.ccpapp.R
import com.example.ccpapp.databinding.FragmentHomeBinding
import com.example.ccpapp.viewmodels.UserViewModel
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private var navc: NavController?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.textCreateAccount.setOnClickListener{ v ->
            val navController = findNavController(v)
            navController.navigate(R.id.signUpFragment)
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.inputUsername.text.toString()
            val pass = binding.inputPassword.text.toString()

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }
            viewModel.authenticateUser(jsonBody)
        }
        return binding.root
    }

    private fun observeAuthUserResult(view: View) {
        viewModel.authUserResult.observe(viewLifecycleOwner) { tokenInfo ->
            Log.d("LOGIN_DEBUG", "TokenInfo recibido: $tokenInfo")
            if (tokenInfo.token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Token válido: ${tokenInfo.token}", Toast.LENGTH_SHORT).show()
                // Aquí puedes navegar dependiendo del rol si lo tienes
            }
        }
    }

        /*if (isSuccess) {
    val navController = Navigation.findNavController(view)
    navController.navigate(R.id.navigation_home)
} else {
    Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
}*/

        /*viewModel.authUserResult.observe(viewLifecycleOwner) { tokenInfo ->

    when (tokenInfo.token) {
        "cliente" -> navController.navigate(R.id.navigation_cliente)
        "transportista" -> navController.navigate(R.id.navigation_transportista)
        "gerente" -> navController.navigate(R.id.navigation_gerente)
        else -> Toast.makeText(context, "Rol no reconocido", Toast.LENGTH_SHORT).show()
    }
}*/


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
        observeAuthUserResult(view)

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