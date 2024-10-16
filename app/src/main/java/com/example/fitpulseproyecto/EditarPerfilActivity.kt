package com.example.fitpulseproyecto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityEditarPerfilBinding

class EditarPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var getContentCamera: ActivityResultLauncher<Uri>
    private lateinit var requestCameraPermission: ActivityResultLauncher<String>
    private lateinit var requestGalleryPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.atras.setOnClickListener {
            val intent = Intent(this, MiPerfilActivity::class.java)
        }
        initActivityResultLaunchers()
    }

    private fun initActivityResultLaunchers() {
        TODO("Not yet implemented")
    }
}