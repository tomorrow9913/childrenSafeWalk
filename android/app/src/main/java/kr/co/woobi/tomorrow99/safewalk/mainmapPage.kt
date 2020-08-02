package kr.co.woobi.tomorrow99.safewalk

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


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
            var position = HashMap<String, Double>()
            position.put("latitude", it.latitude)
            position.put("longitude", it.longitude)

            var body = GetAreaInfoParams(40.0, position)

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
                    val responseData = response.body()

                    if (responseData?.result == "success"){
                        val setCorlor = 4.38
                        val PATH = mutableListOf<LatLng>()

                        for (data in responseData?.data!!){
                            if(data.id == 1) {
                                PATH.add(LatLng(data.source["latitude"]?:0.0, data.source["longitude"]?:0.0))
                                continue
                            }
                            PATH.add(LatLng(data.target["latitude"]?:0.0, data.target["longitude"]?:0.0))
                        }

                        val path = PathOverlay()
                        path.coords = PATH
                        path.color = Color.GREEN
                        path.passedColor = Color.GRAY
                        path.progress = 0.5
                        path.map = naverMap
                    }
                    else {
                        Toast.makeText(this@mainmapPage, "${responseData.comment}.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}

data class RouteTarget (
    var result:String,
    var comment:String,
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
    var radius:Double,
    var position: HashMap<String, Double>
)

interface GetAreaInfoService {
    //@FormUrlEncoded
    @POST(value = "mappingTest.php")
    @Headers("Content-Type: application/json")

    fun requestRoute (
        @Body params: GetAreaInfoParams
    ) : Call<RouteTarget>
}