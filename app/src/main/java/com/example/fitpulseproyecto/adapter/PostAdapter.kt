package com.example.fitpulseproyecto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpulseproyecto.databinding.PostBinding
import com.example.fitpulseproyecto.model.Post

class PostAdapter(private val postList : List<Post>, private val listner : (Post) -> Unit) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder( private val binding: PostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, listener: (Post) -> Unit){
            binding.postUsername.text = post.username
            binding.postUserfoto.setImageResource(post.userfotoRes)
            binding.postFoto.setImageResource(post.postfotoRes)
            binding.postdescription.text = post.description
            itemView.setOnClickListener { listener(post) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position], listner)
    }

    override fun getItemCount(): Int = postList.size
}