package com.example.fitpulseproyecto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitpulseproyecto.databinding.ActivityEstacionDetailBinding
import org.json.JSONObject

class EstacionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstacionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityEstacionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtén los detalles de la estación desde el Intent
        val stationName = intent.getStringExtra("estacionName")
        val stationLocation = intent.getStringExtra("estacionLocation")
        val stationCountry = intent.getStringExtra("estacionCountry")
        val stationCompany = intent.getStringExtra("estacionCompany")

        // Parsear la información de la ubicación
        val locationJson = JSONObject(stationLocation)
        val latitude = locationJson.getDouble("latitude")
        val longitude = locationJson.getDouble("longitude")
        val city = locationJson.getString("city")

        // Asigna los datos a los TextView
        binding.tvStationName.text = stationName
        binding.tvLatitude.text = "Latitud: $latitude"
        binding.tvLongitude.text = "Longitud: $longitude"
        binding.tvCity.text = "Ciudad: $city"
        binding.tvCountry.text = "País: $stationCountry"
        binding.tvCompany.text = "Compañía: $stationCompany"
    }
}
