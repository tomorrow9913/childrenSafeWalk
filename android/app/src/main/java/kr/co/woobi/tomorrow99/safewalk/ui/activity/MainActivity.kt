package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.sungbin.sungbintool.util.ToastUtil
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.woobi.tomorrow99.safewalk.R

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private val locationPermissionCode = 1000

    private val locationSource by lazy {
        FusedLocationSource(this, locationPermissionCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        (supportFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment).getMapAsync(this)

        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    ToastUtil.show(
                        applicationContext,
                        getString(R.string.main_permission_denied),
                        ToastUtil.SHORT,
                        ToastUtil.INFO
                    )
                }

            })
            .setDeniedMessage(R.string.main_permission_denied)
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()

        fab_location.setOnClickListener {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }

        checkGpsIsOn()
    }

    private fun checkGpsIsOn() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
            })
            ToastUtil.show(
                applicationContext,
                getString(R.string.main_need_gps),
                ToastUtil.SHORT,
                ToastUtil.WARNING
            )
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.uiSettings.apply {
            isLogoClickEnabled = false
            isCompassEnabled = false
            isZoomControlEnabled = false
            isZoomGesturesEnabled = true
            logoGravity = Gravity.TOP or Gravity.END
            isScaleBarEnabled = false
            isLocationButtonEnabled = false
            setLogoMargin(16, 16, 16, 16)
        }
        map.locationSource = locationSource
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PermissionChecker.PERMISSION_GRANTED &&
            PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PermissionChecker.PERMISSION_GRANTED
        ) {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

}
