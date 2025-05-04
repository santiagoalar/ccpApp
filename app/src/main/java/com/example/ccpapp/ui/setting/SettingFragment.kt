package com.example.ccpapp.ui.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.ccpapp.adapters.ClientAdapter
import com.example.ccpapp.databinding.FragmentSettingBinding
import com.example.ccpapp.models.User
import com.example.ccpapp.viewmodels.UserViewModel

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private var navc: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navc = view.findNavController()
        viewModel = UserViewModel(requireActivity().application)
        
        // Obtener el usuario actual desde ClientStorage
        val currentUser = getCurrentUser()
        
        // Llenar los campos visuales con la información del usuario
        if (currentUser != null) {
            binding.userName.text = currentUser.name
            binding.userPhone.text = currentUser.phone
            binding.userEmail.text = currentUser.email
            binding.userAddress.text = "Calle 24 #123, Ciudad"
        }
        
        // Configurar el botón de cerrar sesión
        binding.logoutButton.setOnClickListener {
            // Implementar lógica para cerrar sesión
            // Por ejemplo: limpiar tokens, navegar al login, etc.
            navc?.navigate(com.example.ccpapp.R.id.navigation_home)
        }
    }
    
    private fun getCurrentUser(): User? {
        val users = ClientAdapter.UserStorage.getUsers()
        return if (users.isNotEmpty()) users[0] else null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
