package kr.co.woobi.tomorrow99.safewalk.map

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import kotlinx.android.synthetic.main.activity_mainmap.*
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.model.Item
import kr.co.woobi.tomorrow99.safewalk.model.RouteTarget
import kr.co.woobi.tomorrow99.safewalk.model.User
import kr.co.woobi.tomorrow99.safewalk.ui.activity.MainActivity
import kr.co.woobi.tomorrow99.safewalk.ui.dialog.PingInfoDialog
import kr.co.woobi.tomorrow99.safewalk.ui.dialog.SetPing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainMap : AppCompatActivity(), OnMapReadyCallback {
    private val locationPermissionRequestCode = 1000
    private val loginRequestCode = 0

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var user: User

    private var isShowDangerButton = false
    private val marker = Marker()
    private val pingData = HashMap<String, Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainmap)

        val translateUp = AnimationUtils.loadAnimation(
            this,
            R.anim.translate_up
        )


        btn_setDangerous.visibility = View.INVISIBLE


        val options = NaverMapOptions()
            .nightModeEnabled(true)
            .buildingHeight(0.5f)
            .locationButtonEnabled(false)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_map) as MapFragment?
            ?: MapFragment.newInstance(options)
                .also {
                    fm.beginTransaction().add(R.id.fragment_map, it).commit()
                }

        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(
            this,
            LOCATION_PERMISSION_REQUEST_CODE
        )

        img_declaration.setOnClickListener {
            if (isShowDangerButton) {
                btn_setDangerous.visibility = View.INVISIBLE
                isShowDangerButton = false
                marker.map = null
            } else {
                Toast.makeText(this@mainmapPage, "화면을 움직여 위치를 설정하세요", Toast.LENGTH_SHORT).show()
                btn_setDangerous.visibility = View.VISIBLE
                btn_setDangerous.startAnimation(translateUp)
                isShowDangerButton = true
            }
        }

        btn_setDangerous.setOnClickListener {
            if (userInfo.session == null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivityForResult(intent, LOGIN_REQUEST_CODE)
            } else {
                val dlg =
                    SetPing(this)
                var locationNow = HashMap<String, String>()
                locationNow.put("latitude", marker.position.latitude.toString())
                locationNow.put("longitude", marker.position.longitude.toString())
                dlg.start(locationNow, userInfo.session)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                LOGIN_REQUEST_CODE -> {
                    userInfo.session = data?.getStringExtra("session")
                    userInfo.nickname = data?.getStringExtra("nickname")
                    userInfo.name = data?.getStringExtra("name")
                    userInfo.email = data?.getStringExtra("email")
                    userInfo.callNum = data?.getStringExtra("callnum")
                }
            }

            Log.d("디버그", "$requestCode|$data")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        val locationControl = findViewById<LocationButtonView>(R.id.widget_location)
        locationControl.map = naverMap

        // 위치 추적 모드 https://navermaps.github.io/android-map-sdk/guide-ko/4-2.html
        naverMap.locationTrackingMode = LocationTrackingMode.Face

        val listener = Overlay.OnClickListener { overlay ->
            Log.d("디버그", "${pingData[overlay.tag.toString()]}")
            val dlg =
                PingInfoDialog(this)
            dlg.start(pingData[overlay.tag.toString()]!!)
            false
        }


        // 카메라 변경시 이벤트
        naverMap.addOnCameraChangeListener { reason, animated ->
            var center = naverMap.cameraPosition
            if (isShowDangerButton) {
                marker.position = LatLng(center.target.latitude, center.target.longitude)
                marker.icon = MarkerIcons.BLACK
                marker.map = naverMap
            }

            val markerList = HashMap<String, Marker>()
            if (center.zoom > 13) {
                val projection = naverMap.projection
                val coord = projection.fromScreenLocation(PointF(0f, 0f))
                var radius = calDistance(
                    center.target.latitude,
                    center.target.longitude,
                    coord.latitude,
                    coord.longitude
                )

                var body = GetPingInfoParams(
                    (radius * 10 + 1.0).toString(),
                    center.target.latitude.toString(),
                    center.target.longitude.toString()
                )
                Log.d("디버그", "${body}")

                val SERVE_HOST = "http://210.107.245.192:400/"

                var retrofit = Retrofit.Builder()
                    .baseUrl(SERVE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var getPingInfoService = retrofit.create(GetPingInfoService::class.java)

                getPingInfoService.requestRoute(body).enqueue(object : Callback<RouteTarget> {
                    override fun onFailure(call: Call<RouteTarget>, t: Throwable) {
                        Toast.makeText(this@mainmapPage, "네트워크 통신에 실패힜습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onResponse(
                        call: Call<RouteTarget>,
                        response: Response<RouteTarget>
                    ) {
                        val responseData = response.body()

                        if (responseData?.result == "success") {
                            val setCorlor = 0.876

                            for (data in responseData.data!!) {
                                val marker = Marker()
                                marker.position = LatLng(
                                    data.location["latitude"] ?: 0.0,
                                    data.location["longitude"] ?: 0.0
                                )

                                marker.tag = data.id
                                var red = 219.0
                                var green = 219.0

                                if (data.level * 5 < 2.5) {
                                    red = data.level * 500 * setCorlor
                                }
                                if (data.level * 5 > 2.5) {
                                    green = 219 - ((data.level * 500 * setCorlor) - 219)
                                }

                                marker.icon = MarkerIcons.BLACK
                                marker.iconTintColor = Color.rgb(red.toInt(), green.toInt(), 0)
                                marker.map = naverMap
                                marker.onClickListener = listener

                                pingData.put(data.id.toString(), data)

                                markerList.put(data.id.toString(), marker)
                            }
                        } else {
                            Toast.makeText(
                                this@mainmapPage,
                                "${responseData?.comment}.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            } else {
                for (m in markerList) m.value.map = null
                markerList.clear()
            }
        }

        naverMap.addOnLocationChangeListener {
            var center = naverMap.cameraPosition
            if (center.zoom > 13) {
                var retrofit = Retrofit.Builder()
                    .baseUrl("https://naveropenapi.apigw.ntruss.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var getAddresservice = retrofit.create(GetAddressService::class.java)

                getAddresservice.requestRoute(
                    "${center.target.longitude},${center.target.latitude}",
                    "epsg:4326",
                    "roadaddr",
                    "json"
                ).enqueue(object : Callback<AddresResult> {
                    override fun onFailure(call: Call<AddresResult>, t: Throwable) {
                        Toast.makeText(this@mainmapPage, "네트워크 통신에 실패힜습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onResponse(
                        call: Call<AddresResult>,
                        response: Response<AddresResult>
                    ) {
                        val responseData = response.body()
                        Log.d("로그", "$responseData")
                        try {
                            if (responseData.results != null) {
                                var datas = responseData.results!![0]
                                var locationData = ""
                                for (data in datas.region!!) {
                                    if (data.key == "area0") continue
                                    locationData = "${locationData} " + data.value.name
                                }

                                tv_location.text = locationData
                            }
                        } catch (e: Exception) {
                            tv_location.text =
                                "${center.target.latitude}, ${center.target.longitude}"
                        }
                    }
                })
            }

        }
    }

    fun calDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta: Double
        var dist: Double
        theta = lon1 - lon2
        dist =
            Math.sin(deg2rad(lat1)) * Math.sin(
                deg2rad(lat2)
            ) + (Math.cos(
                deg2rad(lat1)
            )
                    * Math.cos(deg2rad(lat2)) * Math.cos(
                deg2rad(theta)
            ))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344 // 단위 mile 에서 km 변환.
        dist = dist * 1000.0 // 단위  km 에서 m 로 변환
        return dist
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    fun rad2deg(rad: Double): Double {
        return (rad * 180.0 / Math.PI)
    }

}