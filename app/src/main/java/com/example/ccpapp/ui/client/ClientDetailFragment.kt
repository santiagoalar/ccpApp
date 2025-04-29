package com.example.ccpapp.ui.client

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.ccpapp.databinding.FragmentClientDetailBinding

class ClientDetailFragment : Fragment() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var captureVideoLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentClientDetailBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentClientDetailBinding.inflate(inflater, container, false)

        // Registrar el ActivityResultLauncher
        captureVideoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val videoUri = result.data?.data
                // Mostrar un mensaje cuando el video se haya guardado
                Toast.makeText(requireContext(), "Video guardado: $videoUri", Toast.LENGTH_SHORT).show()
                binding.tvRecommendationContent.text = "¡Tu video está siendo procesado en este momento, te traeremos recomendaciones de tu lugar muy pronto!"
            } else {
                Toast.makeText(requireContext(), "No se grabó ningún video", Toast.LENGTH_SHORT).show()
            }
        }

        // Asignar el click del botón para grabar video
        binding.btnRecordVideo.setOnClickListener {
            openVideoCamera()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openVideoCamera()
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                openAppSettings()
            }
        }

        binding.btnRecordVideo.setOnClickListener {
            checkCameraPermissionAndRecord()
        }
    }

    private fun checkCameraPermissionAndRecord() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            openVideoCamera()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }


    private fun openVideoCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        captureVideoLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
