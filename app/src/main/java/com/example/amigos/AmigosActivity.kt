package com.example.amigos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.amigos.databinding.ActivityAmigosBinding


class AmigosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAmigosBinding
    private lateinit var amigosList: List<Amigo>
    private lateinit var adapter: AmigosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar ViewBinding
        binding = ActivityAmigosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Crear la lista de amigos
        amigosList = listOf(
            Amigo("Mariana", "Online", R.drawable.amiga1),
            Amigo("Rocio", "Online", R.drawable.amiga2),
            Amigo("Juan", "Online", R.drawable.amigo3),
            Amigo("Roger", "Offline", R.drawable.amigo4),
            Amigo("Emilia", "Offline", R.drawable.amiga5),
            Amigo("Pedro", "Offline", R.drawable.amigo6)

        )

        // Configurar RecyclerView
        binding.friendsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = AmigosAdapter(amigosList) { amigo ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("FRIEND_NAME", amigo.name)
            startActivity(intent)
        }
        binding.friendsRecyclerView.adapter = adapter
    }
}
