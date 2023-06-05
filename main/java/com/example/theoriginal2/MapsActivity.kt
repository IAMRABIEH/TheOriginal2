package com.example.theoriginal2
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.theoriginal2.models.LocationInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var findLocationBtn: Button
    private lateinit var nextActivityBtn: Button
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        findLocationBtn = findViewById(R.id.btn_find_location)
        nextActivityBtn = findViewById(R.id.nextactivity)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        FirebaseApp.initializeApp(this)
        val databaseUrl = "https://theoriginial-copy-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
        dbReference = database.reference.child("test")

        findLocationBtn.setOnClickListener {
            dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val location = childSnapshot.getValue(LocationInfo::class.java)
                        val locationLat = location?.latitude
                        val locationLong = location?.longitude

                        // Check if locationLat and locationLong are not null
                        if (locationLat != null && locationLong != null) {
                            // create a LatLng object from location
                            val latLng = LatLng(locationLat, locationLong)
                            // create a marker at the read location and display it on the map
                            map.addMarker(
                                MarkerOptions().position(latLng)
                                    .title("The user is currently here")
                            )
                            // specify how the map camera is updated
                            val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
                            // update the camera with the CameraUpdate object
                            map.moveCamera(update)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle potential errors here
                    println("DatabaseError: ${databaseError.message}")
                }
            })
        }

        nextActivityBtn.setOnClickListener {
            val intent = Intent(this@MapsActivity, TasksActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
    }
}
