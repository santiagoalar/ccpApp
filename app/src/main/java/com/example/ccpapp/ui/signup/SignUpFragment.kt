package com.example.ccpapp.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.ccpapp.R
import com.example.ccpapp.databinding.FragmentSignupBinding
import org.json.JSONObject

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignUpViewModel
    private var navc: NavController?= null

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

        binding.btnCreateUser.setOnClickListener{

            val name = binding.inputNombres.text.toString()
            val email = binding.inputCorreo.text.toString()
            val phone = binding.inputCelular.text.toString()
            val password = binding.inputPassword.text.toString()
            val role = binding.autoCompleteTextView.text.toString()

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

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        //activity.actionBar?.title = getString(R.string.title_tracks)
        //val args: AddTrackFragmentArgs by navArgs()
        //Log.d("Args", args.albumId.toString())
        viewModel = ViewModelProvider(this, SignUpViewModel.Factory(activity.application))[SignUpViewModel::class.java]
        //viewModel = ViewModelProvider(this, TrackViewModel.Factory(activity.application, 1))[TrackViewModel::class.java]

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navc = Navigation.findNavController(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}