package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.fitpulseproyecto.databinding.ActivityEstacionesBinding
import org.json.JSONObject

class EstacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityEstacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Llenar el Spinner con países (puedes cambiar esto por una lista de países real)
        val countries = arrayOf("CO", "FR", "IT", "ES", "US","CA")
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
            val stationsDetails = mutableListOf<JSONObject>() // Lista para almacenar los detalles de cada estación

            for (i in 0 until networks.length()) {
                val network = networks.getJSONObject(i)
                val location = network.getJSONObject("location")
                val countryName = location.getString("country")

                if (countryName == country) {
                    val stationName = network.getString("name")
                    stationsList.add(stationName)
                    stationsDetails.add(network)  // Guardamos el objeto completo de la estación
                }
            }

            if (stationsList.isNotEmpty()) {
                // Usamos un ArrayAdapter personalizado
                val stationsAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stationsList) {
                    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)
                        textView.setTextColor(android.graphics.Color.WHITE) // Establecer el texto en blanco
                        return view
                    }
                }
                binding.lvEstaciones.adapter = stationsAdapter

                // Configurar el listener para manejar el click en cada estación
                binding.lvEstaciones.setOnItemClickListener { _, _, position, _ ->
                    val selectedStation = stationsDetails[position]
                    val intent = Intent(this, EstacionDetailActivity::class.java)

                    // Pasar los detalles de la estación a la actividad de detalles
                    intent.putExtra("estacionName", selectedStation.getString("name"))
                    intent.putExtra("estacionLocation", selectedStation.getString("location"))
                    intent.putExtra("estacionCountry", selectedStation.getJSONObject("location").getString("country"))
                    intent.putExtra("estacionCompany", selectedStation.getJSONArray("company").getString(0))

                    startActivity(intent) // Iniciar la actividad de detalles
                }
            } else {
                Toast.makeText(this, "No se encontraron estaciones en este país", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("JSON", "Error al parsear la respuesta: ${e.message}")
            Toast.makeText(this, "Error al mostrar las estaciones", Toast.LENGTH_SHORT).show()
        }
    }
}
