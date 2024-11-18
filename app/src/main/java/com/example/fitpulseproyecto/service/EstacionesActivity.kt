package com.example.fitpulseproyecto

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.fitpulseproyecto.databinding.ActivityEstacionesBinding
import org.json.JSONArray
import org.json.JSONObject

class EstacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityEstacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Llenar el Spinner con países (puedes cambiar esto por una lista de países real)
        val countries = arrayOf("CO", "FR", "Italia", "Alemania")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPais.adapter = adapter

        // Configurar el botón de búsqueda
        binding.btnBuscarEstaciones.setOnClickListener {
            val selectedCountry = binding.spinnerPais.selectedItem.toString()
            fetchStations(selectedCountry)
        }
    }

    private fun fetchStations(country: String) {
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://api.citybik.es/v2/networks"
        val request = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Procesar la respuesta JSON
                parseStations(response, country)
            },
            Response.ErrorListener { error ->
                Log.e("Volley", "Error: $error")
                Toast.makeText(this, "Error al obtener las estaciones", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun parseStations(response: String, country: String) {
        try {
            val jsonResponse = JSONObject(response)
            val networks = jsonResponse.getJSONArray("networks")

            val stationsList = mutableListOf<String>()
            for (i in 0 until networks.length()) {
                val network = networks.getJSONObject(i)
                val countryName = network.getString("location").let {
                    JSONObject(it).getString("country")
                }

                if (countryName == country) {
                    val stationName = network.getString("name")
                    stationsList.add(stationName)
                }
            }

            if (stationsList.isNotEmpty()) {
                val stationsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stationsList)
                binding.lvEstaciones.adapter = stationsAdapter
            } else {
                Toast.makeText(this, "No se encontraron estaciones en este país", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("JSON", "Error al parsear la respuesta: ${e.message}")
            Toast.makeText(this, "Error al mostrar las estaciones", Toast.LENGTH_SHORT).show()
        }
    }
}
