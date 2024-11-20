package com.example.fitpulseproyecto.model

    data class ChatMessage(
        val sender: String = "", // Usuario que envía el mensaje
        val message: String = "", // Contenido del mensaje
        val timestamp: Long = 0L // Timestamp para ordenar los mensajes
    )
