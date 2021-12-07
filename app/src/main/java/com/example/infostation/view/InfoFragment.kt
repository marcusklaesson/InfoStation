package com.example.infostation.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.example.infostation.R
import com.example.infostation.adapter.DisplayAdapter
import com.example.infostation.adapter.OnTempClickListener
import com.example.infostation.models.Weather
import com.example.infostation.viewmodel.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_display.*
import kotlinx.android.synthetic.main.item_weather.view.*
import java.util.*
import kotlin.concurrent.fixedRateTimer


@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_display), OnTempClickListener {
    private lateinit var adapter: DisplayAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: DisplayViewModel by activityViewModels()
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLocationPermission()
        setupObservers()
    }

    private fun setupObservers() {
        observerLiveData(viewModel.setupLiveDataLists())
    }

    private fun observerLiveData(data: LiveData<ArrayList<Weather?>>) {
        data.observe(viewLifecycleOwner, { weather ->
            setupTimeStampText(weather)
            setupAdapter(weather)
        })
    }

    private fun setupTimeStampText(weather: ArrayList<Weather?>) {
        weather.map { time ->
            if (time != null) {
                timestamp.text = getString(R.string.weather_updated) + time?.timeStamp
            }
        }.toString()
    }

    private fun setupAdapter(weather: ArrayList<Weather?>) {
        adapter = DisplayAdapter(weather, this)
        recycler_view.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun checkLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getLocation()
                }
                else -> {
                    Log.d("tag", "Permission error")
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLocation() {
        fixedRateTimer("weather", false, 0L, FIFTEEN_MINUTES) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@fixedRateTimer
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    latitude = location.latitude
                    longitude = location.longitude
                    viewModel.setupWeather(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        prefs.prefUnit.toString()
                    )
                }
        }
    }

    override fun onItemClicked() {
        viewModel.updateUnit(latitude.toString(), longitude.toString())
    }

}