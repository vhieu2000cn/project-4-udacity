package com.udacity.project4.utils

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.udacity.project4.base.BaseRecyclerViewAdapter
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment.Companion.LOCATION_DEVICE_REQUEST_CODE

/**
 * Extension function to setup the RecyclerView.
 */
fun <T> RecyclerView.setup(
    adapter: BaseRecyclerViewAdapter<T>
) {
    this.apply {
        layoutManager = LinearLayoutManager(this.context)
        this.adapter = adapter
    }
}

fun Fragment.setTitle(title: String) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
}

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            bool
        )
    }
}

/**
 * Animate changing the view visibility.
 */
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

/**
 * Animate changing the view visibility.
 */
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}

fun Fragment.isAccessFineLocationPermissionGranted(): Boolean {
    return (
            PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
            )
}



@RequiresApi(Build.VERSION_CODES.Q)
fun Fragment.isAccessBackgroundLocationPermissionGranted(): Boolean {
    return (
            PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
            )
}


fun Fragment.checkAccessFineLocationPermissionGranted(
    onSuccess: (() -> Unit)?,
    onFailure: (() -> Unit)?
) {
    if (isAccessFineLocationPermissionGranted()) {
        if (onSuccess != null) {
            onSuccess()
        }
    } else {
        if (onFailure != null) {
            onFailure()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
fun Fragment.checkAccessBackgroundLocationPermissionGranted(
    onSuccess: (() -> Unit)?,
    onFailure: (() -> Unit)?
) {
    if (isAccessBackgroundLocationPermissionGranted()) {
        if (onSuccess != null) {
            onSuccess()
        }
    } else {
        if (onFailure != null) {
            onFailure()
        }
    }
}

fun Fragment.checkLocationDevice(onSuccess: (() -> Unit)?, onFailure: (() -> Unit)? = null) {
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_LOW_POWER
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    val settingsClient = LocationServices.getSettingsClient(requireActivity())
    val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

    locationSettingsResponseTask.addOnSuccessListener {
        if (onSuccess != null) {
            onSuccess()
        }
    }

    locationSettingsResponseTask.addOnFailureListener { err ->
        if (onFailure != null) {
            onFailure()
        } else if (err is ResolvableApiException) {
            startIntentSenderForResult(
                err.resolution.intentSender, LOCATION_DEVICE_REQUEST_CODE,
                null, 0, 0, 0, null
            )
        }
    }
}



