package kr.co.woobi.tomorrow99.safewalk

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_mainmap_page.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class PingInfoDialog(context : Context) {
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private lateinit var address : TextView
    private lateinit var btnOK : Button
    private lateinit var good : TextView
    private lateinit var bad: TextView
    private lateinit var dangerRank:TextView
    private lateinit var skull:List<ImageView>

    fun start(content : String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.ping_info_dialog)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        address = dlg.findViewById(R.id.tv_address)
        dangerRank = dlg.findViewById(R.id.tv_dangerRank)
        good = dlg.findViewById(R.id.tv_goodCnt)
        bad = dlg.findViewById(R.id.tv_badCnt)
        skull = arrayListOf(
            dlg.findViewById(R.id.img_skull1),
            dlg.findViewById(R.id.img_skull2),
            dlg.findViewById(R.id.img_skull3),
            dlg.findViewById(R.id.img_skull4),
            dlg.findViewById(R.id.img_skull5)
        )
        btnOK = dlg.findViewById(R.id.btn_ok)

        btnOK.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    fun changeInfo(data:Item){
        var retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var getAddresservice = retrofit.create(GetAddressService::class.java)

        getAddresservice.requestRoute("${data.location["longitude"]},${data.location["latitude"]}","epsg:4326", "roadaddr", "json").enqueue(object :
            Callback<AddresResult> {
            override fun onFailure(call: Call<AddresResult>, t: Throwable) {
                address.text = "네트워크 통신에 실패힜습니다."
            }

            override fun onResponse(
                call: Call<AddresResult>,
                response: Response<AddresResult>
            ) {
                val responseData = response.body()
                try {
                    if (responseData?.results != null) {
                        var datas = responseData.results!![0]
                        var locationData = ""
                        for (data in datas.region!!){
                            if (data.key == "area0") continue
                            locationData = "${locationData} " + data.value.name
                        }

                        address.text = locationData
                    }
                }
                catch (e: Exception){
                    address.text = "위치 정보를 찾을 수 없습니다."
                }
            }
        })

        var level = data.level
        //dangerRank.text = String.format("%.2f", level)
        if(level*5 >= 0.5) skull[0].setImageResource(R.drawable.skull2)
        if(level*5 >= 1.0) skull[0].setImageResource(R.drawable.skull3)
        if(level*5 >= 1.5) skull[1].setImageResource(R.drawable.skull2)
        if(level*5 >= 2.0) skull[1].setImageResource(R.drawable.skull3)
        if(level*5 >= 2.5) skull[2].setImageResource(R.drawable.skull2)
        if(level*5 >= 3.0) skull[2].setImageResource(R.drawable.skull3)
        if(level*5 >= 3.5) skull[3].setImageResource(R.drawable.skull2)
        if(level*5 >= 4.0) skull[3].setImageResource(R.drawable.skull3)
        if(level*5 >= 4.5) skull[4].setImageResource(R.drawable.skull2)
        if(level*5 == 5.0) skull[4].setImageResource(R.drawable.skull3)

        good.text = data.useful["true"].toString()
        bad.text = data.useful["false"].toString()
    }
}

