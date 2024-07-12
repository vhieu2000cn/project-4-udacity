package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.checkAccessFineLocationPermissionGranted
import com.udacity.project4.utils.checkLocationDevice
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        const val ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1003
        const val LOCATION_DEVICE_REQUEST_CODE = 1002
    }

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        binding.mapView.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        binding.btnSave.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE ->
                checkAccessFineLocationPermissionGranted(
                    {
                        map.isMyLocationEnabled = true
                        checkLocationDevice({
                            showLocation()
                        })
                    },
                    {
                        showAppInfoSnackBar(
                            "location is not available",
                        )
                    }
                )
        }
    }


    @SuppressLint("MissingPermission")
    private fun showLocation() {
        LocationServices.getFusedLocationProviderClient(requireContext())
            .lastLocation.addOnSuccessListener { location ->
                if (location != null){
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            17f
                        )
                    )
                }
            }
    }

    private fun onLocationSelected() {
        marker?.run {
            _viewModel.reminderSelectedLocationStr.value = title
            _viewModel.latitude.value = position.latitude
            _viewModel.longitude.value = position.longitude
        }
        _viewModel.navigationCommand.value = NavigationCommand.Back
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL; true
        }

        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID; true
        }

        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE; true
        }

        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN; true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun requestAccessFineLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showAppInfoSnackBar(
        msg: String,
        onClick: (() -> Unit) = {}
    ) {
        Snackbar.make(
            requireView(), msg, Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.settings) {
            onClick()
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }.show()
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e("DEBUG_STYLE", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("DEBUG_STYLE", "Can't find style. Error: ", e)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapStyle(map)
        map.setOnMapClickListener { latLng ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(this.getString(R.string.dropped_pin))
            )?.apply { showInfoWindow() }
        }
        map.setOnPoiClickListener { pio ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(pio.latLng)
                    .title(pio.name)
            )?.apply { showInfoWindow() }
        }
        map.setOnMyLocationButtonClickListener {
            checkLocationDevice({
                showLocation()
            }, null)
            true
        }
        checkAccessFineLocationPermissionGranted(onSuccess = {
            map.isMyLocationEnabled = true
            checkLocationDevice({
                showLocation()
            })
        }, onFailure = {
            requestAccessFineLocationPermission()
        })
    }

}