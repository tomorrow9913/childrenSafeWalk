package kr.co.woobi.tomorrow99.safewalk

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import kotlinx.android.synthetic.main.activity_mainmap_page.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.Exception
import kotlin.math.abs


class mainmapPage : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap:NaverMap
    private var isShowDangerbtn = false
    val centerMarker = Marker()
    val pingData = HashMap<String, Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainmap_page)

        btn_setDangerous.setVisibility(View.INVISIBLE)

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

        img_declaration.setOnClickListener{
            if (isShowDangerbtn){
                btn_setDangerous.setVisibility(View.INVISIBLE)
                isShowDangerbtn = false
                centerMarker.map = null
            }
            else {
                Toast.makeText(this@mainmapPage, "화면을 움직여 위치를 설정하세요", Toast.LENGTH_SHORT).show()
                btn_setDangerous.setVisibility(View.VISIBLE)
                isShowDangerbtn = true
            }
        }
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

        val listener = Overlay.OnClickListener { overlay ->
            Toast.makeText(this@mainmapPage, "${pingData[overlay.tag.toString()]} 클릭됨", Toast.LENGTH_LONG)
                .show()
            false
        }


        // 카메라 변경시 이벤트
        naverMap.addOnCameraChangeListener{reason, animated ->
            var center = naverMap.cameraPosition
            if(isShowDangerbtn){
                centerMarker.position = LatLng(center.target.latitude, center.target.longitude)
                centerMarker.icon = MarkerIcons.BLACK
                centerMarker.map = naverMap
            }

            val markerList = HashMap<String, Marker>()
            if(center.zoom > 13){
                val projection = naverMap.projection
                val coord = projection.fromScreenLocation(PointF(0f, 0f))
                var radius = calDistance(center.target.latitude,center.target.longitude,coord.latitude,coord.longitude)

                var body = GetPingInfoParams((radius*10+1.0).toString(), center.target.latitude.toString(), center.target.longitude.toString())
                Log.d("디버그", "${body.toString()}")

                val SERVE_HOST = "http://210.107.245.192:400/"

                var retrofit = Retrofit.Builder()
                    .baseUrl(SERVE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var getPingInfoService = retrofit.create(GetPingInfoService::class.java)

                getPingInfoService.requestRoute(body).enqueue(object : Callback<RouteTarget> {
                    override fun onFailure(call: Call<RouteTarget>, t: Throwable) {
                        Toast.makeText(this@mainmapPage, "네트워크 통신에 실패힜습니다.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<RouteTarget>, response: Response<RouteTarget>) {
                        val responseData = response.body()

                        if (responseData?.result == "success"){
                            val setCorlor = 4.38

                            for (data in responseData.data!!){
                                val marker = Marker()
                                marker.position = LatLng(data.location["latitude"]?:0.0, data.location["longitude"]?:0.0)

                                marker.tag = data.id
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
                                marker.setOnClickListener(listener)

                                pingData.put(data.id.toString(), data)

                                markerList.put(data.id.toString(),marker)
                            }
                        }
                        else {
                            Toast.makeText(this@mainmapPage, "${responseData?.comment}.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }else{
                for(m in markerList) m.value.map = null
                markerList.clear()
            }
        }

        naverMap.addOnLocationChangeListener {
            var center = naverMap.cameraPosition
            if(center.zoom > 13){
                var retrofit = Retrofit.Builder()
                    .baseUrl("https://naveropenapi.apigw.ntruss.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var getAddresservice = retrofit.create(GetAddressService::class.java)

                getAddresservice.requestRoute("${center.target.longitude},${center.target.latitude}","epsg:4326", "roadaddr", "json").enqueue(object : Callback<AddresResult> {
                    override fun onFailure(call: Call<AddresResult>, t: Throwable) {
                        Toast.makeText(this@mainmapPage, "네트워크 통신에 실패힜습니다.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<AddresResult>,
                        response: Response<AddresResult>
                    ) {
                        val responseData = response.body()
                        Log.d("로그", "$responseData")
                        try {
                            if (responseData?.results != null) {
                                var datas = responseData.results!![0]
                                var locationData = ""
                                for (data in datas.region!!){
                                    if (data.key == "area0") continue
                                    locationData = "${locationData} " + data.value.name
                                }

                                tv_location.text = locationData
                            }
                        }
                        catch (e:Exception){
                            tv_location.text = "위치 정보를 찾을 수 없습니다."
                        }
                    }
                })
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

data class GetPingInfoParams(
    var radius:String,
    var latitude: String,
    var longitude: String
)

interface GetPingInfoService {
    //@FormUrlEncoded
    @POST(value = "loadPing.php")
    @Headers("Content-Type: application/json")

    fun requestRoute (
        @Body params: GetPingInfoParams
    ) : Call<RouteTarget>
}

data class AddresResult(
    var error: HashMap<String, String>?,
    var status: HashMap<String, String>?,
    var results: List<AddressData>?
)

data class AddressData(
    var name: String,
    var code: HashMap<String, String>?,
    var region: HashMap<String, AreaData>?
)

data class AreaData(
    var name:String,
    val coords:HashMap<String, CenterInfo>
)

data class CenterInfo(
    var crs:String,
    var x:Double,
    var y:Double
)

interface GetAddressService {
    @GET(value = "map-reversegeocode/v2/gc")
    @Headers(
        "X-NCP-APIGW-API-KEY-ID: 7fxx28ikcf",
        "X-NCP-APIGW-API-KEY: kolAzQiZCPl37GQlLV5R3fCbPvFY9F0oSPIQWZWm"
    )

    fun requestRoute (
        @Query("coords")coords:String,
        @Query("sourcecrs")sourcecrs:String, //epsg:4326
        @Query("orders")orders:String, //legalcode
        @Query("output")output:String //json
    ) : Call<AddresResult>
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
