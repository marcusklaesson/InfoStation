package com.example.infostation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.infostation.R
import com.example.infostation.ui.display.DisplayAdapter
import com.example.infostation.ui.display.DisplayViewModel
import com.example.infostation.ui.display.FIFTEEN_MINUTES
import com.example.infostation.utils.combine
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_display.*
import java.util.*
import kotlin.concurrent.fixedRateTimer


@AndroidEntryPoint
class DisplayFragment : Fragment(R.layout.fragment_display) {
    private lateinit var adapter: DisplayAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: DisplayViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()
        setupAdapter()
    }

    private fun setupAdapter() {
        val data = combine(viewModel.time, viewModel.date, viewModel.temp) { time, date, temp ->
            arrayListOf(time, date, temp)
        }
        data.observe(viewLifecycleOwner, {
            it?.map { time ->
                if (time != null) {
                    timestamp.text = getString(R.string.weather_updated) + time?.timeStamp
                }
            }?.toString()
            adapter = DisplayAdapter(it)
            recycler_view.adapter = adapter
            adapter.notifyDataSetChanged()
        })
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
                    Log.d("tag", "error permission")
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
                    viewModel.setupWeather(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                }

        }
    }
}