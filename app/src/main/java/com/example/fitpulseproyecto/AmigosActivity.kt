package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitpulseproyecto.adapter.AmigoAdapter
import com.example.fitpulseproyecto.databinding.ActivityAmigosBinding
import com.example.fitpulseproyecto.model.Amigo

class AmigosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAmigosBinding
    private lateinit var amigosList: List<Amigo>
    private lateinit var adapter: AmigoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar ViewBinding
        binding = ActivityAmigosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Crear la lista de amigos
        amigosList = listOf(
            Amigo("Mariana", "Online", R.drawable.user1),
            Amigo("Rocio", "Online", R.drawable.user2),
            Amigo("Juan", "Online", R.drawable.user),
            Amigo("Roger", "Offline", R.drawable.user2),
            Amigo("Emilia", "Offline", R.drawable.user4),
            Amigo("Pedro", "Offline", R.drawable.user3)

        )

        // Configurar RecyclerView
        binding.friendsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = AmigoAdapter(amigosList) { amigo ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("FRIEND_NAME", amigo.name)
            startActivity(intent)
        }
        binding.friendsRecyclerView.adapter = adapter
    }
}