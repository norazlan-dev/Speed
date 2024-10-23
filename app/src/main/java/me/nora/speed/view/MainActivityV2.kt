package me.nora.speed.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import me.nora.speed.databinding.ActivityMainV2Binding
import java.util.*

class MainActivityV2 : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityMainV2Binding
    private lateinit var locationManager: LocationManager
    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkLocationPermission()

        binding.ivFlip.setOnClickListener {
            setFlip()
        }

        binding.ivFull.setOnClickListener {
            if (isFullscreen) {
                exitFullscreen()
            } else {
                enterFullscreen()
            }
            isFullscreen = !isFullscreen
        }

        binding.ivReset.setOnClickListener {
            binding.tvSpeed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 130f)
            binding.tvSpeedBg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 130f)
            binding.tvUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
        }
    }

    private fun setFlip() {
        if (binding.tvUnit.scaleX != -1F) {
            binding.tvUnit.scaleX = -1F
            binding.tvSpeed.scaleX = -1F
        } else {
            binding.tvUnit.scaleX = 1F
            binding.tvSpeed.scaleX = 1F
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.tvSpeed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 170f)
            binding.tvSpeedBg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 170f)
            binding.tvUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.tvSpeed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 130f)
            binding.tvSpeedBg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 130f)
            binding.tvUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
        }
    }

    private fun enterFullscreen() {
        val decorView: View = window.decorView
        val windowInsetsController = WindowInsetsControllerCompat(window, decorView)

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun exitFullscreen() {
        val decorView: View = window.decorView
        val windowInsetsController = WindowInsetsControllerCompat(window, decorView)

        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }

    private fun checkLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1)
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    override fun onLocationChanged(location: Location) {
        val speed = location.speed * 3.6f // convert m/s to km/h
        binding.tvSpeed.text = String.format(Locale.getDefault(), "%.0f", speed)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}
}