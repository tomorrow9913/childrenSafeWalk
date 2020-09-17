package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.sungbin.sungbintool.extensions.toEditable
import com.sungbin.sungbintool.util.Logger
import com.sungbin.sungbintool.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.`interface`.AddressInterface
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Named("locationApi")
    @Inject
    lateinit var locationApi: Retrofit

    private lateinit var naverMap: NaverMap
    private val locationPermissionCode = 1000

    private val locationSource by lazy {
        FusedLocationSource(this, locationPermissionCode)
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        (supportFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment).getMapAsync(this)

        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    ToastUtil.show(
                        applicationContext,
                        getString(R.string.map_permission_denied),
                        ToastUtil.SHORT,
                        ToastUtil.INFO
                    )
                }

            })
            .setDeniedMessage(R.string.map_permission_denied)
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()

        fab_location.setOnClickListener {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }

        checkGpsIsOn()

        fab_danger.setOnClickListener {
            val layout = LayoutInflater.from(applicationContext).inflate(R.layout.layout_ping_set_dialog, null, false)
            val dialog = MaterialAlertDialogBuilder(this@MapActivity)
            dialog.setView(layout)
            dialog.show()
        }
    }

    private fun checkGpsIsOn() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
            })
            ToastUtil.show(
                applicationContext,
                getString(R.string.map_need_gps),
                ToastUtil.SHORT,
                ToastUtil.WARNING
            )
        }
    }

    @SuppressLint("SetTextI18n")
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

        map.addOnLocationChangeListener {
            val center = naverMap.cameraPosition
            if (center.zoom > 13) {
                locationApi.create(AddressInterface::class.java).run {
                    getAddress(
                        "${center.target.longitude},${center.target.latitude}",
                        "epsg:4326",
                        "roadaddr",
                        "json"
                    )
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            response.results?.get(0)?.let {
                                var locationData = ""

                                for (data in it.region) {
                                    if (data.key == "area0") continue
                                    locationData = "$locationData ${data.value.name}"
                                }

                                tv_location.text = locationData.toEditable()
                            }
                        }, { throwable ->
                            Logger.w(throwable)
                            tv_location.text =
                                "${
                                    center.target.latitude.toString().substring(0..5)
                                },  ${
                                    center.target.longitude.toString().substring(0..5)
                                }".toEditable()
                        }, {
                        })
                }
            }
        }
    }

}
