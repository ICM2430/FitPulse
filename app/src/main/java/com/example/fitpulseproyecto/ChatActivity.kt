package com.example.fitpulseproyecto

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.StringRequest
import com.example.fitpulseproyecto.adapter.ChatAdapter
import com.example.fitpulseproyecto.databinding.ActivityChatBinding
import com.example.fitpulseproyecto.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.vertexai.type.content
import com.google.gson.Gson
import com.android.volley.Request
import com.android.volley.toolbox.Volley

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatList: MutableList<ChatMessage>
    private lateinit var adapter: ChatAdapter
    private lateinit var database: DatabaseReference
    private lateinit var chatId: String
    private lateinit var currentUser: FirebaseUser

    companion object {
        fun generateChatId(userId1: String, userId2: String): String {
            val ids = listOf(
                userId1,
                userId2
            ).sorted() // Ordenamos para garantizar que el chatId sea siempre el mismo
            return "${ids[0]}_${ids[1]}" // Generamos el chatId basado en los dos IDs
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatList = mutableListOf()
        adapter = ChatAdapter(chatList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Obtener el ID del amigo desde el Intent
        val amigoId = intent.getStringExtra("chatId") ?: ""
        if (amigoId.isEmpty()) {
            Toast.makeText(this, "Error: chatId no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar el usuario actual
        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Generar el chatId único basado en los dos usuarios
        chatId = generateChatId(currentUser.uid, amigoId)

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("chats/$chatId/messages")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(ChatMessage::class.java)
                    message?.let { chatList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        binding.btnSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Obtener el usuario actual desde la base de datos de Firebase
                val userId = currentUser.uid
                val userRef = FirebaseDatabase.getInstance().getReference("users/$userId/usuario")

                // Obtener el nombre de usuario de la base de datos
                userRef.get().addOnSuccessListener { snapshot ->
                    val usuario = snapshot.getValue(String::class.java) ?: "Desconocido"

                    // Crear el mensaje con el nombre de usuario
                    val message = ChatMessage(usuario, messageText, System.currentTimeMillis())
                    sendMessage(message)
                }.addOnFailureListener {
                    // En caso de error al obtener el nombre de usuario, usar "Desconocido"
                    val message =
                        ChatMessage("Desconocido", messageText, System.currentTimeMillis())
                    sendMessage(message)
                }
            } else {
                Toast.makeText(this, "Por favor ingresa un mensaje", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendMessage(message: ChatMessage) {
        val messageId = database.push().key
        if (messageId != null) {
            database.child(messageId).setValue(message)
                .addOnSuccessListener {
                    binding.editTextMessage.text.clear()
                    Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()

                    // Obtener el token del receptor desde el campo "token"
                    val receiverId = intent.getStringExtra("chatId") // ID del receptor
                    if (receiverId != null) {
                        val userRef =
                            FirebaseDatabase.getInstance().getReference("users/$receiverId/token")
                        userRef.get().addOnSuccessListener { snapshot ->
                            val fcmToken = snapshot.getValue(String::class.java)
                            if (!fcmToken.isNullOrEmpty()) {
                                // Envía la notificación al receptor
                                sendNotificationToUser(fcmToken, message.message)
                            }
                        }.addOnFailureListener {
                            Log.e(
                                "ChatActivity",
                                "Error al obtener el token del receptor: ${it.message}"
                            )
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Error al enviar el mensaje: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun sendNotificationToUser(token: String, message: String) {
        val url = "https://fcm.googleapis.com/fcm/send"
        val requestBody = mapOf(
            "to" to token,
            "data" to mapOf(
                "title" to "Nuevo mensaje",
                "body" to message
            )
        )

        val jsonBody = Gson().toJson(requestBody)

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response -> Log.d("ChatActivity", "Notificación enviada: $response") },
            { error -> Log.e("ChatActivity", "Error al enviar notificación: ${error.message}") }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to "key=YOUR_SERVER_KEY",
                    "Content-Type" to "application/json"
                )
            }

            override fun getBody(): ByteArray {
                return jsonBody.toByteArray(Charsets.UTF_8)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}


