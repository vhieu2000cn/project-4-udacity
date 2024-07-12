package com.udacity.project4.locationreminders

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.udacity.project4.databinding.ActivityRemindersBinding


/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_POST_NOTIFICATIONS_REQUEST_CODE = 1005
    }

    private lateinit var binding: ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > 32) {
            getNotificationPermission()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.navHostFragment.findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_POST_NOTIFICATIONS_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == 0) {
                // Permission granted
            } else {
                Toast.makeText(this, "Permission post notification is needed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT > 32) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_POST_NOTIFICATIONS_REQUEST_CODE
            )
        }
    }
}
