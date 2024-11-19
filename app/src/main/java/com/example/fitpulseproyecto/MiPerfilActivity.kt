package com.example.fitpulseproyecto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fitpulseproyecto.databinding.ActivityMiPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class MiPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMiPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadProfileImage(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        val user = auth.currentUser
        if (user != null) {
            loadUserData(user.uid)
        }

        binding.atras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Setup for changing the profile image
        binding.circleImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun loadUserData(userId: String) {
        // Load user data from Realtime Database
        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            val nombre = snapshot.child("nombre").value?.toString() ?: "Nombre no disponible"
            val apellido = snapshot.child("apellido").value?.toString() ?: "Apellido no disponible"
            val correo = snapshot.child("correo").value?.toString() ?: "Correo no disponible"
            val usuario = snapshot.child("usuario").value?.toString() ?: "Usuario no disponible"
            val fotoUrl = snapshot.child("fotoUrl").value?.toString() ?: ""

            binding.nombre.text = nombre
            binding.usuario.text = usuario
            binding.correo.text = correo
            binding.apellido.text = apellido
            // Load profile picture
            if (fotoUrl.isNotEmpty()) {
                Picasso.get().load(fotoUrl).into(binding.circleImageView)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadProfileImage(uri: Uri) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val filePath = storageRef.child("profile_images").child("$userId.jpg")
            filePath.putFile(uri).addOnSuccessListener {
                // Get the URL of the uploaded image
                filePath.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    // Save the URL in Realtime Database
                    saveImageUrlToDatabase(userId, imageUrl)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageUrlToDatabase(userId: String, imageUrl: String) {
        // Update the photo URL in the Realtime Database
        database.child("users").child(userId).child("fotoUrl").setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show()
                Picasso.get().load(imageUrl).into(binding.circleImageView)  // Update UI
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar la imagen", Toast.LENGTH_SHORT).show()
            }
    }
}