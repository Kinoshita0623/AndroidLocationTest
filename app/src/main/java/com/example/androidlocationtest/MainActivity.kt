package com.example.androidlocationtest

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationList = ArrayList<Location>()
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = FusedLocationProviderClient(this)

        val locationRequest = LocationRequest().apply{
            interval = 5000
            fastestInterval = 2500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                val location = p0?.lastLocation ?: return
                Toast.makeText(applicationContext, "緯度:${location.latitude}, 経度:${location.longitude}", Toast.LENGTH_LONG).show()

                val beforeLocation: Location? = locationList.lastOrNull()
                when {
                    beforeLocation == null -> locationMovingStatus.text = "取得中"
                    locationEquals(location, locationList.last()) -> locationMovingStatus.text = "停止中"
                    else -> locationMovingStatus.text = "移動中"
                }

                Log.d("MainActivity", "緯度:${location.latitude}, 緯度${location.longitude}")
                locationList.add(location)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
    }



    fun locationEquals(location0: Location, location1: Location, error: Double = 0.001): Boolean{

        //誤差を求める
        val errorLatitude = Math.abs(location0.latitude - location1.latitude)
        val errorLongitude = Math.abs(location0.longitude - location1.longitude)

        return errorLatitude < error && errorLongitude < error
    }

}
