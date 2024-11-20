package com.example.fitpulseproyecto.model

import com.google.android.gms.maps.model.LatLng

data class User(
    val username : String,
    val firstName : String,
    val lastName : String,
    val email : String,
    var location : LatLng,
    val profileImage: String? = null
)
