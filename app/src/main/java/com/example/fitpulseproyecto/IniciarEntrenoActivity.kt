package com.example.fitpulseproyecto

import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.adapter.UserAdapter
import com.example.fitpulseproyecto.databinding.ActivityIniciarEntrenoBinding
import com.example.fitpulseproyecto.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class IniciarEntrenoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIniciarEntrenoBinding

    val database = Firebase.database
    lateinit var myRef : DatabaseReference
    private lateinit var vel: ValueEventListener
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val USERS = "friends/"


    val selectedUserIds = mutableListOf<String>()
    lateinit var adapter : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIniciarEntrenoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = UserAdapter(this, null, 8)
        binding.listFriends.adapter = adapter

        binding.atras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        binding.empezar.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("destino", binding.destino.text.toString())
            intent.putStringArrayListExtra("amigosID", ArrayList(selectedUserIds))
            if (!selectedUserIds.isNullOrEmpty()) {
                // Usar la lista
                for (id in selectedUserIds) {
                    Log.i("listaAmigos", "ID: $id")
                }
            }else{
                Log.i("listaAmigos", "No se seleccionó ningun amigo")
            }
            startActivity(intent)
        }

        binding.listFriends.setOnItemClickListener { adapterView, view, i, l ->
            val cursor = adapter?.getItem(i) as Cursor? // Obtén el Cursor del elemento seleccionado
            cursor?.let {
                val userId = it.getString(it.getColumnIndexOrThrow("userId")) // Obtén el userId

                if (selectedUserIds.contains(userId)) {
                    // Si ya está en la lista, lo eliminamos y actualizamos la vista como deseleccionada
                    selectedUserIds.remove(userId)
                    view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)) // Deseleccionado
                } else {
                    // Si no está en la lista, lo agregamos y actualizamos la vista como seleccionada
                    selectedUserIds.add(userId)
                    view.setBackgroundColor(ContextCompat.getColor(this, R.color.fitpulsegreen)) // Seleccionado
                }
            }
        }

    }
    override fun onResume() {
        super.onResume()
        subscribeToUserChanges()
    }

    override fun onPause() {
        myRef.removeEventListener(vel)
        super.onPause()
    }

    fun subscribeToUserChanges(){
        val userId = auth.currentUser?.uid
        myRef = database.getReference(USERS+userId)
        vel = myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //binding.listUsers.removeAllViews()
                val cursor = MatrixCursor(arrayOf("_id", "name", "photo", "userId"))
                var id = 0
                for(child in snapshot.children){
                    Log.i("friend", child.child("usuario").getValue(String::class.java).toString())
                    val name = child.child("nombre").getValue(String::class.java)
                    val photo = child.child("fotoUrl").getValue(String::class.java)
                    val userId = child.key
                    cursor.addRow(arrayOf(id++, name, photo, userId))

                    adapter.changeCursor(cursor)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //Log error
            }
        })
    }

}