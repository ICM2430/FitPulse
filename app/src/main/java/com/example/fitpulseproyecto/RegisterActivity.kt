package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitpulseproyecto.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Listener para el botón de registro
        binding.registerButton.setOnClickListener {
            val nombre = binding.nombreEditText.text.toString()
            val apellido = binding.apellidoEditText.text.toString()
            val correo = binding.correoEditText.text.toString()
            val contrasena = binding.contrasenaEditText.text.toString()
            val usuario = binding.usuarioEditText.text.toString()

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || usuario.isEmpty()) {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Registro en Firebase Authentication
            auth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        saveUserToDatabase(uid, nombre, apellido, correo, usuario)
                    }
                } else {
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserToDatabase(uid: String, nombre: String, apellido: String, correo: String, usuario: String) {
        val user = mapOf(
            "nombre" to nombre,
            "apellido" to apellido,
            "correo" to correo,
            "usuario" to usuario
        )

        database.reference.child("users").child(uid).setValue(user).addOnSuccessListener {
            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al guardar datos: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
