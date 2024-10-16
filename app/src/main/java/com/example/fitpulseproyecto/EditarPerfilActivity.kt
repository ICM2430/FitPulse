package com.example.fitpulseproyecto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fitpulseproyecto.databinding.ActivityEditarPerfilBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*class EditarPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var getContentGallery: ActivityResultLauncher<String>
    private lateinit var requestCameraPermission: ActivityResultLauncher<String>
    private lateinit var getContentCamera: ActivityResultLauncher<String>
    private lateinit var currentPhotoPath: String
    private lateinit var uriCamera: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Listener para botón de atrás
        binding.atras.setOnClickListener {
            val intent = Intent(this, MiPerfilActivity::class.java)
            startActivity(intent)
        }

        // Inicializar los launchers para abrir la galería
        initActivityResultLaunchers()

        // Configurar el botón para cambiar imagen usando la galería
        binding.btCambiarImagenGaleria .setOnClickListener { checkPermissionAndOpenCamera() }
       // binding.idgallery.setOnClickListener { checkPermissionAndOpenGallery() }
    }

    private fun checkPermissionAndOpenCamera() {
        when {ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
            openCamera()
        }
            else->{
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val photoFile: File = createImageFile()
        uriCamera = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)

        // Aquí se lanza la cámara usando el launcher de resultados
        getContentCamera.launch(uriCamera.toString())
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionAndOpenGallery() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
            openGallery()
        }
            else->{
                requestCameraPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    // Inicializar los launchers para manejar el contenido de la galería
    private fun initActivityResultLaunchers() {
        // Contrato para abrir la galería y seleccionar una imagen
        getContentGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Si se selecciona una imagen, mostrarla en el ImageView
                binding.circleImageView.setImageURI(uri)
            }
        }
    }

    // Función para abrir la galería
    private fun openGallery() {
        // Llama al launcher para abrir la galería y seleccionar una imagen
        getContentGallery.launch("image/*")
    }
}*/

 */
class EditarPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var getContentGallery: ActivityResultLauncher<String>
    private lateinit var requestPermission: ActivityResultLauncher<String>
    private lateinit var getContentCamera: ActivityResultLauncher<Uri>
    private lateinit var currentPhotoPath: String
    private lateinit var uriCamera: Uri

    private var isCameraRequest = false  // Bandera para distinguir entre cámara y galería

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener para botón de atrás
        binding.atras.setOnClickListener {
            val intent = Intent(this, MiPerfilActivity::class.java)
            startActivity(intent)
        }

        // Inicializar los launchers para abrir la galería y cámara
        initActivityResultLaunchers()

        // Configurar el botón para cambiar imagen usando la galería
        binding.btCambiarImagenGaleria.setOnClickListener {
            isCameraRequest = false  // Indica que es una solicitud de galería
            checkPermissionAndOpenGallery()
        }

        // Configurar el botón para cambiar imagen usando la cámara
        binding.btCambiarImagenCamara.setOnClickListener {
            isCameraRequest = true  // Indica que es una solicitud de cámara
            checkPermissionAndOpenCamera()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        try {
            val photoFile: File = createImageFile()
            uriCamera = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
            getContentCamera.launch(uriCamera)
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestPermission.launch(permission)
        }
    }

    private fun openGallery() {
        getContentGallery.launch("image/*")
    }

    private fun initActivityResultLaunchers() {
        getContentGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.circleImageView.setImageURI(uri)
            }
        }

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (isCameraRequest) {
                    openCamera()
                } else {
                    openGallery()
                }
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }

        getContentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.circleImageView.setImageURI(uriCamera)
            } else {
                Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
