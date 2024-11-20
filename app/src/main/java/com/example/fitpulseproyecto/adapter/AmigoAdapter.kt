package com.example.fitpulseproyecto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitpulseproyecto.databinding.ItemAmigoBinding
import com.example.fitpulseproyecto.model.Amigo

class AmigoAdapter(
    private var amigosList: MutableList<Amigo>, // Lista mutable para datos dinámicos
    private val listener: (Amigo) -> Unit // Listener para clics
) : RecyclerView.Adapter<AmigoAdapter.AmigoViewHolder>() {

    // ViewHolder con ViewBinding
    class AmigoViewHolder(private val binding: ItemAmigoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(amigo: Amigo, listener: (Amigo) -> Unit) {
            // Cargar la imagen desde la URL usando Glide
            Glide.with(binding.profileImage.context)
                .load(amigo.fotoUrl) // Cambié 'imageUrl' a 'fotoUrl' acorde al modelo
                .placeholder(com.example.fitpulseproyecto.R.drawable.placeholder_image) // Imagen de carga
                .error(com.example.fitpulseproyecto.R.drawable.error_image) // Imagen en caso de error
                .into(binding.profileImage)

            // Configurar el nombre del amigo
            binding.friendName.text = amigo.nombre // Cambié 'name' a 'nombre'

            // Listener para manejar clics en el elemento
            itemView.setOnClickListener { listener(amigo) }
        }
    }

    // Crear el ViewHolder inflando el layout con ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val binding = ItemAmigoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AmigoViewHolder(binding)
    }

    // Vincular los datos con el ViewHolder
    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        holder.bind(amigosList[position], listener)
    }

    // Devolver el número de elementos
    override fun getItemCount(): Int = amigosList.size

    // Método para actualizar los datos del adapter
    fun updateData(newAmigosList: List<Amigo>) {
        amigosList.clear() // Limpiar lista
        amigosList.addAll(newAmigosList) // Agregar los nuevos amigos
        notifyDataSetChanged() // Notificar que la lista se actualizó
    }
}
