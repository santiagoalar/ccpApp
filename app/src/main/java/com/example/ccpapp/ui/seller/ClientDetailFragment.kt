package com.example.ccpapp.ui.seller

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccpapp.adapters.ClientAdapter
import com.example.ccpapp.databinding.FragmentSellerClientDetailBinding
import com.example.ccpapp.models.Client
import com.example.ccpapp.models.VideoResponse
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.viewmodels.UserViewModel
import com.example.ccpapp.viewmodels.VideoRecordViewModel
import com.example.ccpapp.viewmodels.VisitRecordsViewModel
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClientDetailFragment : Fragment() {

    private var _binding: FragmentSellerClientDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel
    private lateinit var visitRecordsViewModel: VisitRecordsViewModel
    private lateinit var videoRecordViewModel: VideoRecordViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var captureVideoLauncher: ActivityResultLauncher<Intent>
    private lateinit var tokenManager: TokenManager
    private var client: Client? = null
    private var currentVideoPath: String = ""
    
    // Handler y Runnable para la verificación periódica
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var videoStatusChecker: Runnable
    private var isCheckingStatus = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerClientDetailBinding.inflate(inflater, container, false)

        // Inicializar el Runnable para verificar el estado del video
        videoStatusChecker = object : Runnable {
            override fun run() {
                if (isCheckingStatus) {
                    val videoId = ClientAdapter.UserStorage.getVideoId()
                    if (!videoId.isNullOrEmpty()) {
                        videoRecordViewModel.checkIfVideoFinished()
                    }
                    // Programar la próxima verificación después de 10 segundos
                    handler.postDelayed(this, 10000)
                }
            }
        }

        captureVideoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val videoFile = File(currentVideoPath)
                
                if (videoFile.exists() && videoFile.length() > 0) {
               
                    val loadingDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Procesando video")
                        .setMessage("Enviando video para análisis de IA...")
                        .setCancelable(false)
                        .create()
                    loadingDialog.show()

                    videoRecordViewModel.sendVideo(videoFile)
                    
                    videoRecordViewModel.videoSendResult.observe(viewLifecycleOwner) { response ->
                        loadingDialog.dismiss()
                        if (response != null) {

                            showVideoSentSuccessDialog()

                            binding.tvProcessingStatus.text = "Video enviado, procesando..."
                            binding.tvProcessingStatus.setTextColor(resources.getColor(android.R.color.holo_orange_dark))
                        } else {

                            Toast.makeText(requireContext(), "Error al enviar el video", Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    videoRecordViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                        if (!isLoading) {
                            loadingDialog.dismiss()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al grabar el video", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No se grabó ningún video", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        visitRecordsViewModel = ViewModelProvider(requireActivity())[VisitRecordsViewModel::class.java]
        videoRecordViewModel = ViewModelProvider(requireActivity())[VideoRecordViewModel::class.java]
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

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openVideoCamera()
            } else {
                showCameraPermissionDeniedDialog()
            }
        }

        binding.btnRecordVideo.setOnClickListener {
            checkCameraPermissionAndRecord()
        }

        // Comprobar si hay un videoId guardado para iniciar verificaciones
        val videoId = ClientAdapter.UserStorage.getVideoId()
        if (!videoId.isNullOrEmpty()) {
            updateProcessingStatus("Verificando estado del video...", android.R.color.holo_orange_dark)
            startStatusChecking()
        }

        // Observar el resultado del envío del video
        videoRecordViewModel.videoSendResult.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                showVideoSentSuccessDialog()
                updateProcessingStatus("Video enviado, procesando...", android.R.color.holo_orange_dark)
                startStatusChecking()
            } else {
                Toast.makeText(requireContext(), "Error al enviar el video", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Observar el estado de verificación del video
        videoRecordViewModel.videoStatus.observe(viewLifecycleOwner) { status ->
            when (status?.status) {
                "COMPLETED" -> {
                    // Video procesado completamente, mostrar resultado del análisis
                    val analysisResult = status.analysisResult ?: "Análisis completado"
                    updateProcessingStatus(analysisResult, android.R.color.white)
                    stopStatusChecking() // Detener verificaciones porque ya está completo
                }
                "PROCESSING" -> {
                    // Video en procesamiento
                    updateProcessingStatus("Video en procesamiento...", android.R.color.holo_orange_dark)
                }
                "ERROR" -> {
                    // Error en el procesamiento
                    updateProcessingStatus("Error en el procesamiento:", android.R.color.holo_red_light)
                    stopStatusChecking() // Detener verificaciones por error
                }
                "PENDING" -> {
                    // Pendiente de procesamiento
                    updateProcessingStatus("En cola para procesamiento...", android.R.color.holo_orange_light)
                }
                else -> {
                    // Estado desconocido
                    updateProcessingStatus("Estado: ${status?.status ?: "Desconocido"}", android.R.color.white)
                }
            }
        }
    }

    private fun checkCameraPermissionAndRecord() {
        when {
            requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == 
                    android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openVideoCamera()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                showCameraPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun showCameraPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permiso de cámara necesario")
            .setMessage("Para grabar el video de análisis de IA, necesitamos acceder a tu cámara. Por favor, concede el permiso en la siguiente pantalla.")
            .setPositiveButton("Aceptar") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showCameraPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permiso denegado")
            .setMessage("Sin acceso a la cámara no podemos grabar el video para el análisis de IA. Puedes habilitarlo en la configuración de la aplicación.")
            .setPositiveButton("Ir a configuración") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
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

    private fun openVideoCamera() {
        val videoFile = createVideoFile()
        val videoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.ccpapp.fileprovider",
            videoFile
        )
        
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
        }
        
        captureVideoLauncher.launch(intent)
    }
    
    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        
        val videoFile = File.createTempFile(
            "VIDEO_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        )
        
        currentVideoPath = videoFile.absolutePath
        return videoFile
    }

    private fun showVideoSentSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Video enviado")
            .setMessage("El video ha sido enviado correctamente. Pronto recibirás recomendaciones basadas en el análisis de IA.")
            .setPositiveButton("Aceptar", null)
            .create()
            .show()
    }

    private fun updateProcessingStatus(message: String, colorResId: Int) {
        binding.tvProcessingStatus.text = message
        binding.tvProcessingStatus.setTextColor(resources.getColor(colorResId))
    }

    private fun startStatusChecking() {
        if (!isCheckingStatus) {
            isCheckingStatus = true
            handler.post(videoStatusChecker)
        }
    }

    private fun stopStatusChecking() {
        isCheckingStatus = false
        handler.removeCallbacks(videoStatusChecker)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopStatusChecking() // Asegurarse de detener las verificaciones al destruir la vista
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Reanudar verificaciones si hay un video en proceso
        val videoId = ClientAdapter.UserStorage.getVideoId()
        if (!videoId.isNullOrEmpty()) {
            startStatusChecking()
        }
    }

    override fun onPause() {
        super.onPause()
        // Pausar verificaciones mientras el fragmento no está visible
        stopStatusChecking()
    }
}
