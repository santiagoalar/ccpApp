package com.example.ccpapp.ui.seller

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccpapp.databinding.FragmentSellerClientDetailBinding
import com.example.ccpapp.models.Client
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.viewmodels.UserViewModel
import com.example.ccpapp.viewmodels.VisitRecordsViewModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClientDetailFragment : Fragment() {

    private var _binding: FragmentSellerClientDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel
    private lateinit var visitRecordsViewModel: VisitRecordsViewModel
    private lateinit var tokenManager: TokenManager
    private var client: Client? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerClientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        visitRecordsViewModel =
            ViewModelProvider(requireActivity())[VisitRecordsViewModel::class.java]
        tokenManager = TokenManager(requireContext())

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegisterVisit.setOnClickListener {
            showVisitNoteDialog()
        }

        viewModel.selectedClient.observe(viewLifecycleOwner) { selectedClient ->
            client = selectedClient
            updateUI(selectedClient)
            Log.d("ClientDetailFragment", "Cliente seleccionado: ${selectedClient?.clientName}")
        }
    }

    private fun updateUI(client: Client?) {
        client?.let {
            binding.tvClientName.text = it.clientName
            binding.tvClientPhone.text = it.clientPhone
            binding.tvClientEmail.text = it.clientEmail
            binding.tvClientStore.text = it.storeName
            binding.tvClientAddress.text = it.address
            binding.tvClientCity.text = it.city
            binding.tvClientCountry.text = it.country
        }
    }

    private fun showVisitNoteDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Escribe una nota para la visita"
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        editText.isSingleLine = false
        editText.minLines = 3

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Registrar visita")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val notes = editText.text.toString()
                if (notes.isNotEmpty()) {
                    registerVisit(notes)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "La nota no puede estar vacía",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun showVisitConfirmationDialog(notes: String) {
        client?.let {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmar registro")
                .setMessage("¿Desea registrar la visita en el local de ${it.clientName}?")
                .setPositiveButton("Aceptar") { _, _ ->
                    submitVisit(notes)
                }
                .setNegativeButton("Cancelar", null)
                .create()

            dialog.show()
        }
    }

    private fun registerVisit(notes: String) {
        showVisitConfirmationDialog(notes)
    }

    private fun submitVisit(notes: String) {
        client?.let { client ->
            val currentDate =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
            val salesmanId = tokenManager.getUserId()
            val visitJson = JSONObject().apply {
                put("clientId", client.id)
                put("visitDate", currentDate)
                put("notes", notes)
                put("salesmanId", salesmanId)
                put("clientName", client.clientName)
                put("store", "Tienda de ${client.clientName}")
            }

            visitRecordsViewModel.saveVisitRecord(visitJson)
            Toast.makeText(requireContext(), "Visita registrada correctamente", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}