package kr.co.woobi.tomorrow99.safewalk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.overlay.PolylineOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


class mainmapPage : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainmap_page)

        val INTENT_INFO = getIntent()
        val SESSION = INTENT_INFO.getStringExtra("session")
        val NICK = INTENT_INFO.getStringExtra("nickname")
        val NAME = INTENT_INFO.getStringExtra("name")
        var MAIL = INTENT_INFO.getStringExtra("email")
        val PHONE:String? = INTENT_INFO.getStringExtra("callnum")

        val dialog = AlertDialog.Builder(this@mainmapPage)
        dialog.setTitle("알람")
        dialog.setMessage("Session=${SESSION}\nNick=$NICK\nName=${NAME}\nMail=$MAIL\nPhone=${PHONE}")
        //dialog.show()

        val options = NaverMapOptions()
            .nightModeEnabled(true)
            .buildingHeight(0.5f)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.fragment_map, MapFragment.newInstance()).commit()
            }

        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val init = LatLng(35.1541181, 129.0319860)
        val cameraPosition = CameraPosition(init, 17.0);

        naverMap.setCameraPosition(cameraPosition);

        var body = HashMap<String, HashMap<String, Double>>()

        //출발지 도착지
        var source = HashMap<String, Double>()
        source.put("latitude", 0.0)
        source.put("longitude", 0.0)

        var target = HashMap<String, Double>()
        target.put("latitude", 0.0)
        target.put("longitude", 0.0)

        body.put("source", source)
        body.put("target", target)

        val SERVE_HOST:String = "http://210.107.245.192:400/"
        var retrofit = Retrofit.Builder()
            .baseUrl(SERVE_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var routeService = retrofit.create(RouteService::class.java)

        routeService.requestRoute(body).enqueue(object : Callback<RouteTarget> {
            override fun onFailure(call: Call<RouteTarget>, t: Throwable) {
                Toast.makeText(this@mainmapPage, "네트워크 통신에 실패힜습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<RouteTarget>, response: Response<RouteTarget>) {
                val responseData = response.body()

                if (responseData?.result == "success"){
                    val PATH = mutableListOf<LatLng>()

                    for (data in responseData?.item){
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

            }
        })
    }
}

data class RouteTarget (
    var result:String,
    var item:List<Item>
)

data class Item(
    var id:Int,
    var source: HashMap<String, Double>,
    var target:HashMap<String, Double>
)

interface RouteService {
    //@FormUrlEncoded
    @POST(value = "mappingTest.php")
    @Headers("Content-Type: application/json")

    fun requestRoute (
        @Body params: HashMap<String, HashMap<String, Double>>
    ) : Call<RouteTarget>
}