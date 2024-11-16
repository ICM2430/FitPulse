package com.example.fitpulseproyecto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitpulseproyecto.adapter.ChatAdapter
import com.example.fitpulseproyecto.databinding.ActivityChatBinding
import com.example.fitpulseproyecto.model.ChatMessage

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //intent ayuda a obtener nombre de amigo
        val friendName = intent.getStringExtra("FRIEND_NAME")
        binding.friendNameTextView.text = friendName

        // Configurar el RecyclerView
        chatAdapter = ChatAdapter(messages)
        binding.messagesRecyclerView.adapter = chatAdapter
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Enviar mensaje al hacer clic en el botón
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                // Añadir el mensaje enviado a la lista
                messages.add(ChatMessage(message, true)) // True: enviado por el usuario
                chatAdapter.notifyItemInserted(messages.size - 1)
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
                binding.messageEditText.text.clear()

                // Simular respuesta del amigo
                simulateFriendResponse()
            }
        }
    }

    private fun simulateFriendResponse() {
        Handler(Looper.getMainLooper()).postDelayed({
            messages.add(ChatMessage("si", false))
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
        }, 2000)
    }
}