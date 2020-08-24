package kr.co.woobi.tomorrow99.safewalk.map.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.map.AddresResult
import kr.co.woobi.tomorrow99.safewalk.map.GetAddressService
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class SetPing(context: Context) {
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private lateinit var address : TextView
    private lateinit var btnAddTag : Button
    private lateinit var btnOK : Button
    private lateinit var dangerRank:TextView
    private lateinit var tagTable:LinearLayout
    private lateinit var skull:List<ImageView>
    private lateinit var image:ImageView

    private var tagList = mutableListOf<Int>()

    fun start(data: HashMap<String, String>, session: String, activity: Activity) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.set_ping_dialog)     //다이얼로그에 사용할 xml 파일을 불러옴

        address = dlg.findViewById(R.id.tv_address)
        dangerRank = dlg.findViewById(R.id.tv_dangerRank)
        skull = arrayListOf(
            dlg.findViewById(R.id.img_skull1),
            dlg.findViewById(R.id.img_skull2),
            dlg.findViewById(R.id.img_skull3),
            dlg.findViewById(R.id.img_skull4),
            dlg.findViewById(R.id.img_skull5)
        )
        btnOK = dlg.findViewById(R.id.btn_ok)

        image = dlg.findViewById(R.id.img_pingImg)

        image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                activity,
                Intent.createChooser(intent, "Select Picture"),
                1,
                null
            )
        }

        btnOK.setOnClickListener {
            val SERVE_HOST = "http://210.107.245.192:400/"
            var retrofit = Retrofit.Builder()
                .baseUrl(SERVE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var addPingService = retrofit.create(AddPingService::class.java)

            var body = AddPingIn(
                session,
                data["latitude"]!!,
                data["longitude"]!!,
                dangerRank.text.toString().toDouble() / 5,
                tagList.distinct()
            )



            addPingService.addPing(body).enqueue(object : Callback<AddPingOut> {
                override fun onFailure(call: Call<AddPingOut>, t: Throwable) {
                    Toast.makeText(dlg.context, "네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<AddPingOut>, response: Response<AddPingOut>) {
                    var responseData = response.body()
                    if (responseData?.result == "success") {
                        val PING_ID = responseData.id

                        //todo 이미지 전송
                        /************************
                         * 파일 명 : id.jpg
                         * 현재 pingID : PING_ID
                         * 보낼 이미지 : 현재 img_pingImg에 표시되는 이미지.
                         ***********************/

                        dlg.dismiss()
                    } else {
                        Toast.makeText(dlg.context, "${responseData?.comment}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }

        btnAddTag = dlg.findViewById(R.id.btn_addTag)

        btnAddTag.setOnClickListener{
            var builder = AlertDialog.Builder(dlg.context)
            val tagList = arrayOf(
                "교통안전",
                "학교안전",
                "생활안전",
                "시설안전",
                "도보불편",
                "사회안전",
                "자연재해",
                "사고위험",
                "도로위생",
                "위험물 처리 시설",
                "무서움",
                "흡연지역",
                "노후시설",
                "차량안전",
                "악취"
            )
            tagTable = dlg.findViewById(R.id.row_tag)

            builder.setTitle("추가할 태그를 선택해 주세요")

            // Set items form alert dialog
            builder.setItems(tagList, { _, which ->
                try {
                    val textView = TextView(dlg.context)
                    textView.text = tagList[which]
                    textView.customBg()
                    tagTable.addView(textView)
                    this.tagList.add(which)
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(dlg.context, "$e", Toast.LENGTH_SHORT).show()
                }
            })

            // Create a new AlertDialog using builder object
            val dialog = builder.create()

            // Finally, display the alert dialog
            dialog.show()
        }


        var retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var getAddresservice = retrofit.create(GetAddressService::class.java)

        getAddresservice.requestRoute(
            "${data["longitude"]},${data["latitude"]}",
            "epsg:4326",
            "roadaddr",
            "json"
        ).enqueue(object :
            Callback<AddresResult> {
            override fun onFailure(call: Call<AddresResult>, t: Throwable) {
                address.text = "네트워크 통신에 실패힜습니다."
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<AddresResult>,
                response: Response<AddresResult>
            ) {
                val responseData = response.body()
                try {
                    if (responseData?.results != null) {
                        var datas = responseData.results!![0]
                        var locationData = ""
                        for (data in datas.region!!) {
                            if (data.key == "area0") continue
                            locationData = "${locationData} " + data.value.name
                        }

                        address.text = locationData
                    }
                } catch (e: Exception) {
                    address.text = "${String.format("%.5f", data["latitude"]?.toDouble())}, ${
                        String.format(
                            "%.5f",
                            data["longitude"]?.toDouble()
                        )
                    }."
                }
            }
        })

        skull[0].setOnClickListener{
            dangerRank.text = "1.00"
            skull[0].setImageResource(R.drawable.skull3)
            for(i in 1..4) skull[i].setImageResource(R.drawable.skull1)
        }

        skull[1].setOnClickListener{
            dangerRank.text = "2.00"
            for(i in 0..1) skull[i].setImageResource(R.drawable.skull3)
            for(i in 2..4) skull[i].setImageResource(R.drawable.skull1)
        }

        skull[2].setOnClickListener{
            dangerRank.text = "3.00"
            for(i in 0..2) skull[i].setImageResource(R.drawable.skull3)
            for(i in 3..4) skull[i].setImageResource(R.drawable.skull1)
        }

        skull[3].setOnClickListener{
            dangerRank.text = "4.00"
            for(i in 0..3) skull[i].setImageResource(R.drawable.skull3)
            skull[4].setImageResource(R.drawable.skull1)
        }

        skull[4].setOnClickListener{
            dangerRank.text = "5.00"
            for(i in 0..4) skull[i].setImageResource(R.drawable.skull3)
        }

        dlg.show()
    }
}

data class AddPingIn(
    var session: String,
    var latitude: String,
    var longitude: String,
    var level: Double,
    var tag: List<Int>
)

data class AddPingOut(
    var result: String,
    var id: Int?,
    var comment: String?
)

interface AddPingService {
    //@FormUrlEncoded
    @POST(value = "addPing.php")
    @Headers("Content-Type: application/json")

    fun addPing(
        @Body params: AddPingIn
    ) : Call<AddPingOut>
}

interface SendImg {
    @Multipart
    @POST("userProfile/setUserProfileImage.php/")

    fun sendImageRequest(
        @Part imageFile: MultipartBody.Part
    ): Call<String>
}