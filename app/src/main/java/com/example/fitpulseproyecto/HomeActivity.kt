package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitpulseproyecto.adapter.PostAdapter
import com.example.fitpulseproyecto.databinding.ActivityHomeBinding
import com.example.fitpulseproyecto.model.Post
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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


        //val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM yyyy", Locale("es", "CO"))
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

        binding.config.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
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