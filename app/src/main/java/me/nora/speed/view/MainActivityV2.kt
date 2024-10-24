package me.nora.speed.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
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
import me.nora.speed.R
import me.nora.speed.databinding.ActivityMainV2Binding
import java.util.*

class MainActivityV2 : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityMainV2Binding
    private lateinit var locationManager: LocationManager
    private var isFullscreen = false
    private var isMph = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainV2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        isMph = sharedPreferences.getBoolean("IS_MPH", false)
        checkUnit()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkLocationPermission()

        binding.tvUnit.setOnClickListener {
            isMph = !isMph
            checkUnit()
            sharedPreferences.edit().putBoolean("IS_MPH", isMph).apply()
        }

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

    private fun checkUnit() {
        if (isMph) {
            binding.tvUnit.text = getString(R.string.mp_h)
        } else {
            binding.tvUnit.text = getString(R.string.km_h)
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

        binding.ivReset.visibility = View.INVISIBLE
        binding.ivFlip.visibility = View.INVISIBLE
    }

    private fun exitFullscreen() {
        val decorView: View = window.decorView
        val windowInsetsController = WindowInsetsControllerCompat(window, decorView)

        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

        binding.ivReset.visibility = View.VISIBLE
        binding.ivFlip.visibility = View.VISIBLE
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
        var speed: Float = location.speed * 3.6f

        if (isMph) {
            speed /= 1.609f
        }

        binding.tvSpeed.text = String.format(Locale.getDefault(), "%.0f", speed)
    }
}