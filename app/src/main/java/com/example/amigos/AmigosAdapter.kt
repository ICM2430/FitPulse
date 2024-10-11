package com.example.amigos
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.amigos.databinding.ItemAmigoBinding

class AmigosAdapter(private val amigosList: List<Amigo>, private val listener: (Amigo) -> Unit) :
    RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder>() {

    // ViewHolder con ViewBinding
    class AmigoViewHolder(private val binding: ItemAmigoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(amigo: Amigo, listener: (Amigo) -> Unit) {
            binding.profileImage.setImageResource(amigo.imageRes)
            binding.friendName.text = amigo.name
            binding.friendStatus.text = amigo.status
            itemView.setOnClickListener { listener(amigo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        // Inflar el layout usando ViewBinding
        val binding = ItemAmigoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AmigoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        holder.bind(amigosList[position], listener)
    }

    override fun getItemCount(): Int = amigosList.size
}
