package com.example.fitpulseproyecto.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.bumptech.glide.Glide
import com.example.fitpulseproyecto.R
import com.example.fitpulseproyecto.databinding.UserowBinding
import com.google.android.gms.maps.model.LatLng

class UserAdapter(context: Context?, c:Cursor?, flags:Int) :
    CursorAdapter(context, c, flags) {
        private lateinit var binding: UserowBinding

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        binding = UserowBinding.inflate(LayoutInflater.from(context), parent, false)
        return binding.root
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        if (cursor != null) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val userPhoto = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
            val userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"))
            binding.username.text = username
            if (context != null) {
                Glide.with(context)
                    .load(userPhoto)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.userfoto)
            }


        }
    }
}