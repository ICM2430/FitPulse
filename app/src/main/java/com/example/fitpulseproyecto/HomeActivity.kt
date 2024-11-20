package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitpulseproyecto.adapter.PostAdapter
import com.example.fitpulseproyecto.databinding.ActivityHomeBinding
import com.example.fitpulseproyecto.model.Post
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    //POST LIST
    private lateinit var postList : List<Post>
    private lateinit var postAdapter: PostAdapter

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("Token: $token")
                saveTokenToDatabase(token)
            } else {
                println("Error al obtener el token: ${task.exception}")
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }


        //val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM , yyyy", Locale("es", "CO"))
        val currentDate = dateFormat.format(Date())

        postList = listOf(
            Post("chikiman", R.drawable.user1, R.drawable.post1, "Entreno matutino con los panas"),
            Post("teragod", R.drawable.user2, R.drawable.post2, "Al fin logre subir monserrate"),
            Post("stinky", R.drawable.user3, R.drawable.post3, "Vean lo que me merque el otro dia"),
            Post("anuelAA", R.drawable.user4, R.drawable.post4, "Enfocado en mi sin envidiar a nadie")
        )
        binding.postRecyclerView.layoutManager = GridLayoutManager(this, 1)
        postAdapter = PostAdapter(postList) {}
        binding.postRecyclerView.adapter = postAdapter

        binding.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        binding.config.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        binding.fecha.text = currentDate

        binding.iniciarActividad.setOnClickListener {
            val intent = Intent(this, IniciarEntrenoActivity::class.java)
            startActivity(intent)
        }
        binding.miPerfil.setOnClickListener {
            val intent = Intent(this, MiPerfilActivity::class.java)
            startActivity(intent)
        }
        binding.verHistorial.setOnClickListener {
            val intent = Intent(this, VerHistorialActivity::class.java)
            startActivity(intent)
        }
        binding.verAmigos.setOnClickListener{
            val intent = Intent(this, VerAmigosActivity::class.java)
            startActivity(intent)
        }
        binding.compartirEntreno.setOnClickListener {
            val intent = Intent(this, CompartirEntrenoActivity::class.java)
            startActivity(intent)
        }
        binding.salir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

private fun saveTokenToDatabase(token: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/token")
    databaseRef.setValue(token)
        .addOnSuccessListener { println("Token guardado exitosamente.") }
        .addOnFailureListener { println("Error al guardar el token: ${it.message}") }
}

