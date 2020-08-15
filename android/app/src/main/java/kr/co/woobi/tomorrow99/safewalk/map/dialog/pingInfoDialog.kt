package kr.co.woobi.tomorrow99.safewalk.map.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.map.AddresResult
import kr.co.woobi.tomorrow99.safewalk.map.GetAddressService
import kr.co.woobi.tomorrow99.safewalk.map.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.lang.Exception
import android.graphics.drawable.GradientDrawable as GradientDrawable1

class PingInfoDialog(context : Context) {
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private lateinit var address : TextView
    private lateinit var btnOK : Button
    private lateinit var good : TextView
    private lateinit var goodIcon : ImageView
    private lateinit var bad: TextView
    private lateinit var badIcon: ImageView
    private lateinit var dangerRank:TextView
    private lateinit var tagTable:LinearLayout
    private lateinit var skull:List<ImageView>

    fun start(data: Item) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.ping_info_dialog)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        address = dlg.findViewById(R.id.tv_address)
        dangerRank = dlg.findViewById(R.id.tv_dangerRank)
        good = dlg.findViewById(R.id.tv_goodCnt)
        goodIcon = dlg.findViewById(R.id.img_good)
        bad = dlg.findViewById(R.id.tv_badCnt)
        badIcon = dlg.findViewById(R.id.img_bad)
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
                    address.text = "${String.format("%.5f", data.location["latitude"])}, ${String.format("%.5f", data.location["longitude"])}"
                }
            }
        })

        var level = data.level*5
        dangerRank.text = String.format("%.2f", level)
        if(level >= 0.5) skull[0].setImageResource(R.drawable.skull2)
        if(level >= 1.0) skull[0].setImageResource(R.drawable.skull3)
        if(level >= 1.5) skull[1].setImageResource(R.drawable.skull2)
        if(level >= 2.0) skull[1].setImageResource(R.drawable.skull3)
        if(level >= 2.5) skull[2].setImageResource(R.drawable.skull2)
        if(level >= 3.0) skull[2].setImageResource(R.drawable.skull3)
        if(level >= 3.5) skull[3].setImageResource(R.drawable.skull2)
        if(level >= 4.0) skull[3].setImageResource(R.drawable.skull3)
        if(level >= 4.5) skull[4].setImageResource(R.drawable.skull2)
        if(level == 5.0) skull[4].setImageResource(R.drawable.skull3)

        good.text = String.format("%.0f", data.useful["true"])
        bad.text = String.format("%.0f", data.useful["false"])

        //todo 유용성 평가 추가할 것

        tagTable = dlg.findViewById(R.id.row_tag)

        val tagList:Array<String> = arrayOf("교통안전", "학교안전", "생활안전", "시설안전", "도보불편", "사회안전", "자연재해", "사고위험", "도로위생", "위험물 처리 시설", "무서움", "흡연지역", "노후시설", "차량안전", "악취")
        for (tag in data.tag)
        {
            val textView = TextView(dlg.context)
            textView.text = tagList[tag%15]
            textView.customBg()
            tagTable.addView(textView)
        }

        dlg.show()
    }
}

fun TextView.customBg() {
    background = GradientDrawable1().apply {
        shape = GradientDrawable1.RECTANGLE
        cornerRadius = 10f
        setStroke(
            4, ContextCompat.getColor(context,
                R.color.fst
            )
        )
    }
}

interface AddUsefulService {
    //@FormUrlEncoded
    @POST(value = "goodOrBadPing.php")
    @Headers("Content-Type: application/json")

    fun addUseful (
        @Body params: HashMap<String, String>
    ) : Call<HashMap<String, String>>
}