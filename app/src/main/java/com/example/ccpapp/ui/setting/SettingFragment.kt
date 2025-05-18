package com.example.ccpapp.ui.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.ccpapp.adapters.ClientAdapter.UserStorage
import com.example.ccpapp.adapters.ProductAdapter.CartStorage
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

        val currentUser = getCurrentUser()

        if (currentUser != null) {
            binding.userName.text = currentUser.name
            binding.userPhone.text = currentUser.phone
            binding.userEmail.text = currentUser.email
            binding.userAddress.text = "Calle 24 #123, Ciudad"
        }

        binding.logoutButton.setOnClickListener {
            CartStorage.clearCart()
            UserStorage.clear()
            navc?.navigate(com.example.ccpapp.R.id.navigation_home)
        }
    }
    
    private fun getCurrentUser(): User? {
        val users = UserStorage.getUsers()
        return if (users.isNotEmpty()) users[0] else null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
