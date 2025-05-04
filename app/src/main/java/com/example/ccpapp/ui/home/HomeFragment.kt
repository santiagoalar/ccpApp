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
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ccpapp.MainActivity
import com.example.ccpapp.R
import com.example.ccpapp.adapters.ClientAdapter.UserStorage
import com.example.ccpapp.databinding.FragmentHomeBinding
import com.example.ccpapp.viewmodels.UserViewModel
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private var navc: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.textCreateAccount.setOnClickListener { v ->
            val navController = findNavController(v)
            navController.navigate(R.id.signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.inputUsername.text.toString()
            val pass = binding.inputPassword.text.toString()

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }
            viewModel.authenticateUser(jsonBody)
        }

        /*binding.textForgotPassword.setOnClickListener{v->
            val navController = findNavController(v)
            navController.navigate(R.id.clientFragment)
        }*/
        return binding.root
    }

    fun onUserLoggedIn(userRole: String) {
        // Llamar a MainActivity para actualizar el menú
        (binding.root.context as? MainActivity)?.updateBottomNavigationMenu(userRole)
    }

    private fun observeAuthUserResult(view: View) {
        viewModel.authUserResult.observe(viewLifecycleOwner) { tokenInfo ->
            Log.d("LOGIN_DEBUG", "TokenInfo recibido: $tokenInfo")
            if (tokenInfo.token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.validateToken(tokenInfo.token)
                Log.d("VALIDATE_TOKEN", "SE PROCEDE A VALIDAR EL TOKEN")
                // Aquí puedes navegar dependiendo del rol si lo tienes
            }
        }

        viewModel.tokenUserResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                onUserLoggedIn(user.role.name)
                UserStorage.addUser(user)
                when (user.role.toString()) {
                    "CLIENTE" -> findNavController().navigate(R.id.clientFragment)
                    "VENDEDOR" -> findNavController().navigate(R.id.sellerFragment)
                    "DIRECTIVO" -> findNavController().navigate(R.id.directorFragment)
                    "TRANSPORTISTA" -> findNavController().navigate(R.id.carrierFragment)
                    else -> Toast.makeText(context, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Credenciales incorrectas :(", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        viewModel = ViewModelProvider(
            this,
            UserViewModel.Factory(activity.application)
        )[UserViewModel::class.java]

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

        navc = findNavController(view)
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
