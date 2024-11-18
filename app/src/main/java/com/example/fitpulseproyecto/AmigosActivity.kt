package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitpulseproyecto.adapter.AmigoAdapter
import com.example.fitpulseproyecto.databinding.ActivityAmigosBinding
import com.example.fitpulseproyecto.model.Amigo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AmigosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAmigosBinding
    private lateinit var amigosList: MutableList<Amigo>
    private lateinit var adapter: AmigoAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmigosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        amigosList = mutableListOf()
        adapter = AmigoAdapter(amigosList) { amigo ->
            val chatId = amigo.id
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId!!)

        loadFriends()

        binding.btnAddFriend.setOnClickListener {
            val friendName = binding.editTextFriendName.text.toString().trim()
            if (friendName.isNotEmpty()) {
                searchAndAddFriend(friendName)
            } else {
                Toast.makeText(this, "Por favor ingresa un nombre de amigo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFriends() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amigosList.clear()
                if (!snapshot.exists()) {
                    Toast.makeText(this@AmigosActivity, "No tienes amigos agregados aún.", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                    return
                }

                for (userSnapshot in snapshot.children) {
                    try {
                        val amigo = userSnapshot.getValue(Amigo::class.java)
                        amigo?.let { amigosList.add(it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("AmigosActivity", "Error al convertir datos: ${e.message}")
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AmigosActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchAndAddFriend(friendName: String) {
        val usersDatabase = FirebaseDatabase.getInstance().getReference("users")

        usersDatabase.orderByChild("usuario").equalTo(friendName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            Log.d("AmigosActivity", "Datos recibidos: ${userSnapshot.value}")
                            val amigo = userSnapshot.getValue(Amigo::class.java)
                            amigo?.id = userSnapshot.key ?: ""  // Asignando el ID del usuario desde Firebase

                            if (amigo != null) {
                                if (amigo.id != currentUserId) {
                                    addNewFriend(amigo)
                                } else {
                                    Toast.makeText(
                                        this@AmigosActivity,
                                        "No puedes agregarte a ti mismo como amigo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Log.e("AmigosActivity", "Datos del amigo incompletos: ${userSnapshot.value}")
                                Toast.makeText(this@AmigosActivity, "Error al leer datos del amigo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@AmigosActivity, "Amigo no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AmigosActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    fun generateChatId(userId1: String, userId2: String): String {
        val ids = listOf(userId1, userId2).sorted() // Ordenamos para garantizar que el chatId sea siempre el mismo
        return "${ids[0]}_${ids[1]}" // Generamos el chatId basado en los dos IDs
    }


    private fun addNewFriend(amigo: Amigo) {
        if (amigo.id.isNotEmpty() && amigo.nombre.isNotEmpty() && amigo.usuario.isNotEmpty()) {
            val newAmigo = Amigo(
                id = amigo.id,
                nombre = amigo.nombre,
                usuario = amigo.usuario,
                fotoUrl = amigo.fotoUrl,
                apellido = amigo.apellido,
                correo = amigo.correo
            )

            // Generamos un chatId único entre el usuario actual y el amigo
            val chatId = generateChatId(currentUserId!!, amigo.id)

            // Ahora puedes guardar el amigo en la base de datos y asociarlo con un chatId
            database.child(amigo.id).setValue(newAmigo)
                .addOnSuccessListener {
                    amigosList.add(newAmigo)
                    adapter.notifyItemInserted(amigosList.size - 1)
                    Toast.makeText(this, "Amigo agregado correctamente", Toast.LENGTH_SHORT).show()

                    // Ahora que se agregó el amigo, puedes manejar el chat si es necesario.
                    // Puedes almacenar el chatId generado de alguna manera en la base de datos de chats.
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al agregar el amigo: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("AmigosActivity", "Datos inválidos para agregar amigo: $amigo")
            Toast.makeText(this, "Error al generar la clave para el amigo", Toast.LENGTH_SHORT).show()
        }
    }


}
