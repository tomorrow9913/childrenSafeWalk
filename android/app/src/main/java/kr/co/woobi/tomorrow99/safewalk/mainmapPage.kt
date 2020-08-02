package kr.co.woobi.tomorrow99.safewalk

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import kotlin.math.abs


class mainmapPage : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap:NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainmap_page)

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

        mapFragment.getMapAsync (this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
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

        val locationControl = findViewById(R.id.widget_location) as LocationButtonView
        locationControl.map = naverMap

        //위치 추적 모드 https://navermaps.github.io/android-map-sdk/guide-ko/4-2.html
        naverMap.locationTrackingMode = LocationTrackingMode.Face

        naverMap.addOnLocationChangeListener {
            var center = naverMap.cameraPosition
            val markerList = ArrayList<Marker>()
            if(center.zoom > 15){
                val projection = naverMap.projection
                val coord = projection.fromScreenLocation(PointF(0f, 0f))
                var radius = calDistance(center.target.latitude,center.target.longitude,coord.latitude,coord.longitude)

                Log.d("카메라","$center|$radius")

                var body = GetAreaInfoParams((radius+1.0).toString(), it.latitude.toString(), it.longitude.toString())

                val SERVE_HOST:String = "http://210.107.245.192:400/"
                var retrofit = Retrofit.Builder()
                    .baseUrl(SERVE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var getAreaInfoService = retrofit.create(GetAreaInfoService::class.java)

                getAreaInfoService.requestRoute(body).enqueue(object : Callback<RouteTarget> {
                    override fun onFailure(call: Call<RouteTarget>, t: Throwable) {
                        Toast.makeText(this@mainmapPage, "네트워크 통신에 실패힜습니다.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<RouteTarget>, response: Response<RouteTarget>) {
                        for(m in markerList) m.map = null
                        val responseData = response.body()

                        if (responseData?.result == "success"){
                            val setCorlor = 4.38

                            for (data in responseData.data!!){
                                val marker = Marker()
                                marker.position = LatLng(data.location["latitude"]?:0.0, data.location["longitude"]?:0.0)

                                var red = 219.0
                                var green = 219.0
                                if(data.level * 100 > 50){
                                    red = 219-(data.level * 100 * setCorlor)
                                    green = (data.level * 100 * setCorlor)
                                }
                                if (data.level * 100 < 50) {
                                    red = (data.level * 100 * setCorlor)
                                    green = 219-(data.level * 100 * setCorlor)
                                }

                                marker.icon = MarkerIcons.BLACK
                                marker.iconTintColor = Color.rgb(red.toInt() ,green.toInt(),0)
                                marker.map = naverMap
                                markerList.add(marker)
                            }
                        }
                        else {
                            Toast.makeText(this@mainmapPage, "${responseData?.comment}.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }else{
                for(m in markerList) m.map = null
            }

        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}

data class RouteTarget (
    var result:String,
    var comment:String?,
    var data:List<Item>?
)

data class Item(
    var id:Int,
    var location: HashMap<String, Double>,
    var level:Double,
    var tag:List<Int>,
    var useful:HashMap<String, Double>
)

data class GetAreaInfoParams(
    var radius:String,
    var latitude: String,
    var longitude: String
)

interface GetAreaInfoService {
    //@FormUrlEncoded
    @POST(value = "loadPing.php")
    @Headers("Content-Type: application/json")

    fun requestRoute (
        @Body params: GetAreaInfoParams
    ) : Call<RouteTarget>
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
        Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + (Math.cos(
            deg2rad(lat1)
        )
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta)))
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
