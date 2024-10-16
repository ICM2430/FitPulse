    package com.example.fitpulseproyecto

    import android.Manifest
    import android.content.Context
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.graphics.Canvas
    import android.graphics.Color
    import android.graphics.drawable.Drawable
    import android.hardware.Sensor
    import android.hardware.SensorEvent
    import android.hardware.SensorEventListener
    import android.hardware.SensorManager
    import android.location.Geocoder
    import android.location.Location
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.os.Looper
    import android.os.StrictMode
    import android.util.Log
    import android.widget.Toast
    import androidx.activity.result.ActivityResultCallback
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.annotation.ColorRes
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.google.android.gms.location.FusedLocationProviderClient
    import com.google.android.gms.location.LocationServices
    import com.google.android.gms.maps.CameraUpdateFactory
    import com.google.android.gms.maps.GoogleMap
    import com.google.android.gms.maps.OnMapReadyCallback
    import com.google.android.gms.maps.SupportMapFragment
    import com.google.android.gms.maps.model.BitmapDescriptor
    import com.google.android.gms.maps.model.LatLng
    import com.google.android.gms.maps.model.MarkerOptions
    import com.example.fitpulseproyecto.databinding.ActivityMapsBinding
    import com.example.taller_2_daniel_teran.model.MyLocation
    import com.example.taller_2_daniel_teran.model.RouteResponse
    import com.example.taller_2_daniel_teran.service.OpenRouteService
    import com.google.android.gms.location.LocationCallback
    import com.google.android.gms.location.LocationRequest
    import com.google.android.gms.location.LocationResult
    import com.google.android.gms.location.Priority
    import com.google.android.gms.maps.model.BitmapDescriptorFactory
    import com.google.android.gms.maps.model.MapStyleOptions
    import com.google.android.gms.maps.model.PolylineOptions
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import org.json.JSONObject
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.io.BufferedWriter
    import java.io.File
    import java.io.FileWriter
    import java.util.Date

    class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

        private lateinit var mMap: GoogleMap
        private lateinit var binding: ActivityMapsBinding

        val RADIUS_OF_EARTH_KM = 6371

        private lateinit var locationClient: FusedLocationProviderClient
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback
        private lateinit var lastLocationObtained: LatLng
        private val locations : MutableList<JSONObject> = mutableListOf()

        private lateinit var geocoder: Geocoder
        private var destino: LatLng? = null
        private var inicio = LatLng(0.0, 0.0)

        private lateinit var sensorManager: SensorManager
        private var magnetometer: Sensor? = null
        private var lightSensor: Sensor? = null
        private var temperatureSensor: Sensor? = null
        private var accelerometer: Sensor? = null
        private lateinit var sensorEventListener: SensorEventListener

        private val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ActivityResultCallback {
                startLocationUpdates()
            }
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            super.onCreate(savedInstanceState)

            binding = ActivityMapsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            sensorEventListener = createSensorEventListener()

            geocoder = Geocoder(baseContext)
            val destinoAddress = intent.getStringExtra("destino")
            destino = destinoAddress?.let { findLocation(it) }

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            locationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = createLocationRequest()
            locationCallback = createLocationCallback()
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        override fun onResume() {
            super.onResume()
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
            if (temperatureSensor != null) {
                sensorManager.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            if (accelerometer != null) {
                sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }

        override fun onPause() {
            super.onPause()
            sensorManager.unregisterListener(sensorEventListener)
        }

        override fun onMapReady(googleMap: GoogleMap) {
            val javeriana = LatLng(4.628626515364146, -74.06468596237461)
            lastLocationObtained = javeriana
            mMap = googleMap
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.setAllGesturesEnabled(true)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(javeriana))
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
        }

        private fun createLocationRequest(): LocationRequest {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(1000).build()
            return request
        }

        private fun createLocationCallback(): LocationCallback {
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    val location = result.lastLocation
                    if (location != null) {
                        updateLocation(location)
                        val locationAux = LatLng(location.latitude, location.longitude)
                        drawMarker(locationAux, "yo")
                    }
                }
            }
            return callback
        }

        private fun startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
        }

        private fun updateLocation(location: Location) {
            val currentLocation = LatLng(location.latitude, location.longitude)
            if (inicio.latitude == 0.0 && inicio.longitude == 0.0) {
                inicio = currentLocation
            }
            Log.i("ubicacion", "latitud ${location.latitude}, longitud ${location.longitude}")
            if (distance(lastLocationObtained.latitude, lastLocationObtained.longitude, currentLocation.latitude, currentLocation.longitude) > 0.005) {
                Log.i("Distancia", "LA DISTANCIA ES MAYOR A 5m")
                mMap.clear()
                drawMarker(currentLocation, "Yo")
                destino?.let { createRoute(inicio, it) }
            } else {
                Log.i("Distancia", "LA DISTANCIA ES MENOOOOOOR")
            }
            lastLocationObtained = currentLocation
        }

        private fun createSensorEventListener(): SensorEventListener {
            return object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (this@MapsActivity::mMap.isInitialized) {
                        if (lightSensor != null && event != null) {
                            if (event.sensor.type == Sensor.TYPE_LIGHT) {
                                if (event.values[0] < 5000) {
                                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(baseContext, R.raw.dark))
                                } else {
                                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(baseContext, R.raw.light))
                                }
                            }
                        }

                        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                            val x = event.values[0]
                            val y = event.values[1]
                            val direction = Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble()))

                            var adjustedDirection = direction
                            if (adjustedDirection < 0) {
                                adjustedDirection += 360
                            }
                            Log.i("Brújula", adjustedDirection.toString())
                            binding.brujuladireccion.text = "dir ${adjustedDirection.toString()}"
                        }

                        if (event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                            val temperature = event.values[0]
                            Log.i("Temperatura", "$temperature °C")
                            binding.temperaturaVal.text = "$temperature °C"
                            if(temperature >= 40){
                                Log.i("Temperatura", "CALIENTE")
                                binding.temperaturaVal.setBackgroundResource(R.color.red)
                                Toast.makeText(baseContext, "Hace mucho calor, es momento de hidratarse", Toast.LENGTH_LONG).show()
                            }else if(temperature < 0){
                                Log.i("Temperatura", "FRIO")
                                binding.temperaturaVal.setBackgroundResource(R.color.translucent_blue)
                            }else{
                                Log.i("Temperatura", "NORMAL")
                                binding.temperaturaVal.setBackgroundResource(R.color.fitpulsegreen)
                            }
                        }


                        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                            val zAcc = event.values[2]
                            Log.i("Acelerómetro", "Aceleracion: $zAcc")
                            binding.acelerometroZval.text = "Ac: $zAcc"
                            if(zAcc < -20 || zAcc > 20){
                                Log.i("peligro", "El ciclista se cayo")
                                writeJSONObject(lastLocationObtained)
                            }
                        }

                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }

        fun drawMarker(location: LatLng, description: String?) {
            val addressMarker = mMap.addMarker(
                MarkerOptions().position(location).icon(bitmapDescriptorFromVector(this, R.drawable.ic_bike))
            )!!
            if (description != null) {
                addressMarker.title = description
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
        }
        fun writeJSONObject(currentLocation : LatLng) {
            val myLocation = MyLocation(Date(System.currentTimeMillis()), currentLocation.latitude, currentLocation.longitude)
            locations.add(myLocation.toJSON())
            val filename = "locations.json"
            val file = File(baseContext.getExternalFilesDir(null), filename)
            val output = BufferedWriter(FileWriter(file))
            output.write(locations.toString())
            output.close()
        }

        fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
            val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
            vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
            val latDistance = Math.toRadians(lat1 - lat2)
            val lngDistance = Math.toRadians(long1 - long2)
            val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            return Math.round(RADIUS_OF_EARTH_KM * c * 100.0) / 100.0
        }

        fun findLocation(address: String): LatLng? {
            val addresses = geocoder.getFromLocationName(address, 2)
            if (addresses != null && addresses.isNotEmpty()) {
                val addr = addresses[0]
                return LatLng(addr.latitude, addr.longitude)
            }
            return null
        }

        fun createRoute(start: LatLng, end: LatLng) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(OpenRouteService::class.java).getRoute(
                    "5b3ce3597851110001cf624878ccea7dcf3c4f4c8cbe61281706a078",
                    "${start.longitude},${start.latitude}",
                    "${end.longitude},${end.latitude}"
                )
                if (call.isSuccessful) {
                    drawRoute(call.body())
                }
            }
        }

        private fun drawRoute(routeResponse: RouteResponse?) {
            val polylineOptions = PolylineOptions()
            routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
                polylineOptions.add(LatLng(it[1], it[0]))
            }
            runOnUiThread {
                mMap.addPolyline(polylineOptions.color(Color.CYAN))
            }
        }

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
