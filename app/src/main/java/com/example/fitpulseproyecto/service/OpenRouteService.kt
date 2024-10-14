package com.example.taller_2_daniel_teran.service

import com.example.taller_2_daniel_teran.model.RouteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenRouteService {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(@Query("api_key") key : String,
                 @Query("start", encoded = true) start : String,
                 @Query("end", encoded = true) end : String
    ) : Response<RouteResponse>
}