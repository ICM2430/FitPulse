    package com.example.fitpulseproyecto

    import android.Manifest
    import android.content.Context
    import android.content.Intent
    import android.content.IntentSender
    import android.content.pm.PackageManager
    import android.database.MatrixCursor
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
    import androidx.activity.result.IntentSenderRequest
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
    import com.google.android.gms.common.api.ResolvableApiException
    import com.google.android.gms.location.LocationCallback
    import com.google.android.gms.location.LocationRequest
    import com.google.android.gms.location.LocationResult
    import com.google.android.gms.location.LocationSettingsRequest
    import com.google.android.gms.location.LocationSettingsResponse
    import com.google.android.gms.location.Priority
    import com.google.android.gms.location.SettingsClient
    import com.google.android.gms.maps.model.BitmapDescriptorFactory
    import com.google.android.gms.maps.model.MapStyleOptions
    import com.google.android.gms.maps.model.Marker
    import org.osmdroid.views.overlay.Overlay
    import org.osmdroid.views.overlay.Polyline
    import com.google.android.gms.maps.model.PolylineOptions
    import com.google.android.gms.tasks.Task
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.ValueEventListener
    import com.google.firebase.database.database
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import org.json.JSONArray
    import org.json.JSONObject
    import org.osmdroid.bonuspack.routing.OSRMRoadManager
    import org.osmdroid.bonuspack.routing.RoadManager
    import org.osmdroid.util.GeoPoint
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.io.BufferedReader
    import java.io.BufferedWriter
    import java.io.File
    import java.io.FileReader
    import java.io.FileWriter
    import java.util.Date

    class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

        private lateinit var mMap: GoogleMap
        private lateinit var binding: ActivityMapsBinding

        //CONSTANTS
        val RADIUS_OF_EARTH_KM = 6371

        //LOCATION
        private lateinit var locationClient: FusedLocationProviderClient
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback
        private var myMarker: Marker? = null
        private var friendMarker: Marker? = null


        private lateinit var geocoder: Geocoder
        private var destino: LatLng? = null
        private var inicio: LatLng? = null
        private var currentLocation: LatLng? = null

        //SENSORS
        private lateinit var sensorManager: SensorManager
        private var magnetometer: Sensor? = null
        private var lightSensor: Sensor? = null
        private var temperatureSensor: Sensor? = null
        private var accelerometer: Sensor? = null
        private lateinit var sensorEventListener: SensorEventListener

        //Para OSM Bonuspack
        private lateinit var roadManager: RoadManager
        private var roadOverlay: Polyline? = null

        //DATABASE
        val database = Firebase.database
        var friendRef : DatabaseReference? = null
        var loggedRef : DatabaseReference? = null
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private lateinit var friendvel: ValueEventListener
        private lateinit var loggedUserVel: ValueEventListener
        val USERS = "users/"
        var amigosID : ArrayList<String>? = null

        val getLocationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ActivityResultCallback {
                locationSettings()
            }
        )

        //LocationSettings
        val locationSettings = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(),ActivityResultCallback {
                if(it.resultCode == RESULT_OK){
                    startLocationUpdates()
                }else{
                    startActivity(Intent(baseContext, MenuActivity::class.java))
                }
            }
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            super.onCreate(savedInstanceState)

            binding = ActivityMapsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            roadManager = OSRMRoadManager(this, "ANDROID")


            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            sensorEventListener = createSensorEventListener()

            geocoder = Geocoder(baseContext)
            val destinoAddress = intent.getStringExtra("destino")
            amigosID = intent.getStringArrayListExtra("amigosID")
            destino = destinoAddress?.let { findLocation(it) }

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            locationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = createLocationRequest()
            locationCallback = createLocationCallback()

            getLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
            stopLocationUpdates()
            loggedRef?.removeEventListener(loggedUserVel)
            friendRef?.removeEventListener(friendvel)
            sensorManager.unregisterListener(sensorEventListener)
        }

        private fun stopLocationUpdates() {
            locationClient.removeLocationUpdates(locationCallback)
        }


        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap
            mMap.uiSettings.setAllGesturesEnabled(true)
            mMap.uiSettings.isZoomControlsEnabled = true
            if (!amigosID.isNullOrEmpty()) {
                // Usar la lista
                for (id in amigosID!!) {
                    Log.i("listaAmigos", "ID: $id")
                    subscribeToUserChanges(id)
                }
            }else{
                Log.i("listaAmigos", "No se seleccionó ningun amigo")
            }
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
                    if (result != null) {
                        val location = result.lastLocation!!
                        updateUI(location)
                    }
                }
            }
            return callback
        }

        fun locationSettings(){
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener { locationSettingsResponse ->// All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startLocationUpdates()
            }
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException){
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val isr : IntentSenderRequest = IntentSenderRequest.Builder(exception.
                        resolution).build()
                        locationSettings.launch(isr)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }

        private fun startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
        }

        private fun updateUI(location: Location) {
            Log.i("location-update", "${location.latitude} , ${location.longitude}")
            currentLocation = LatLng(location.latitude, location.longitude)
            val userId = auth.currentUser!!.uid
            loggedRef = database.getReference(USERS+userId)
            var user: Map<String, Any?>
            var coordinates : LatLng? = null
            loggedRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Log.i("loggedUser", snapshot.toString())
                    //Log.i("loggedUser", snapshot.child("/usuario").getValue(String::class.java).toString())
                    val nombre = snapshot.child("/nombre").getValue(String::class.java).toString()
                    val apellido = snapshot.child("/apellido").getValue(String::class.java).toString()
                    val correo = snapshot.child("/correo").getValue(String::class.java).toString()
                    val usuario = snapshot.child("/usuario").getValue(String::class.java).toString()
                    val fotoUrl = snapshot.child("/fotoUrl").getValue(String::class.java).toString()
                    val databaseLocationLatitude = snapshot.child("/location/latitude").getValue(Double::class.java)
                    val databaseLocationLongitude = snapshot.child("/location/longitude").getValue(Double::class.java)

                    if(databaseLocationLatitude == null && databaseLocationLongitude == null) {
                        coordinates = LatLng(location.latitude, location.longitude)
                        Log.i("loggedUser", "ubicacion de la base de datos $databaseLocationLatitude , $databaseLocationLongitude")
                    }else if (distance(databaseLocationLatitude!!, databaseLocationLongitude!!, location.latitude, location.longitude) > 0.03){
                        coordinates = LatLng(location.latitude, location.longitude)
                        Log.i("loggedUser", "(CAMBIO) de  $databaseLocationLatitude , $databaseLocationLongitude a -> ${location.latitude} , ${location.longitude}")
                    }else{
                        coordinates = LatLng(databaseLocationLatitude, databaseLocationLongitude)
                    }
                    user = mapOf(
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "correo" to correo,
                        "usuario" to usuario,
                        "fotoUrl" to fotoUrl,
                        "location" to coordinates
                    )


                    database.reference.child("users").child(userId).setValue(user)
                }
                override fun onCancelled(error: DatabaseError) {
                    //Log error
                }
            })

            val locationLatLng = LatLng(location.latitude, location.longitude)

            //drawMarker(location, "Marker in my location")
            if(inicio == null){
                inicio = locationLatLng
            }else{
                drawRoute(GeoPoint(currentLocation!!.latitude, currentLocation!!.longitude), GeoPoint(destino!!.latitude, destino!!.longitude))
            }

            myMarker?.remove()
            myMarker = mMap.addMarker(MarkerOptions()
                .position(locationLatLng)
                .title("Marker in my location")
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_bike))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng))
            mMap.moveCamera(CameraUpdateFactory.zoomTo(18f))
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
                            //Log.i("Acelerómetro", "Aceleracion: $zAcc")
                            binding.acelerometroZval.text = "Ac: $zAcc"
                            if(zAcc < -20 || zAcc > 20){
                                Log.i("peligro", "El ciclista se cayo")
                            }
                        }

                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }

        fun drawMarker(location: LatLng, description: String?) {
            Log.i("friend-location", location.latitude.toString())
            //friendMarker?.remove()
            friendMarker = mMap.addMarker(MarkerOptions()
                .position(location)
                .title("Marker in my friend location")
                .icon(bitmapDescriptorFromVector(baseContext, R.drawable.baseline_pedal_bike_24))
            )
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

        fun drawRoute(start: GeoPoint, finish: GeoPoint) {
            val routePoints = ArrayList<GeoPoint>()
            routePoints.add(start)
            routePoints.add(finish)
            val road = roadManager.getRoad(routePoints)
            Log.i("MapsApp", "Route length: ${road.mLength} km")
            Log.i("MapsApp", "Duration: ${road.mDuration / 60} min")
            if (mMap != null) {
                if (roadOverlay != null) {
                    // Remove the previous overlay from the map
                    (roadOverlay as? Overlay)?.let { overlay ->
                        mMap.clear() // Clear all overlays
                    }
                }
                roadOverlay = RoadManager.buildRoadOverlay(road)
                roadOverlay!!.outlinePaint.color = Color.RED
                roadOverlay!!.outlinePaint.strokeWidth = 12F
                // Add the new overlay to the map
                // Note: Google Maps does not support osmdroid overlays directly
                // You need to convert the osmdroid overlay to a Google Maps Polyline
                val polylineOptions = PolylineOptions()
                for (point in roadOverlay!!.points) {
                    polylineOptions.add(LatLng(point.latitude, point.longitude))
                }
                polylineOptions.color(Color.RED)
                polylineOptions.width(12F)
                mMap.addPolyline(polylineOptions)
            }
        }
        fun subscribeToUserChanges(userId : String){
            friendRef = database.getReference(USERS+userId)
            friendvel = friendRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val latitude = snapshot.child("location/latitude").getValue(Double::class.java)
                    val longitude = snapshot.child("location/longitude").getValue(Double::class.java)
                    if(latitude != null && longitude != null){
                        val locationLatLng = LatLng(latitude, longitude)
                        drawMarker(locationLatLng, "friend")
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    //Log error
                }
            })
        }

    }
