package kr.co.woobi.tomorrow99.safewalk.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.PermissionChecker
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.sungbin.sungbintool.extensions.afterTextChanged
import com.sungbin.sungbintool.extensions.get
import com.sungbin.sungbintool.extensions.toEditable
import com.sungbin.sungbintool.util.Logger
import com.sungbin.sungbintool.util.ToastLength
import com.sungbin.sungbintool.util.ToastType
import com.sungbin.sungbintool.util.ToastUtil
import com.sungbin.sungbintool.util.Util.doDelay
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map.view.*
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.`interface`.AddressInterface
import kr.co.woobi.tomorrow99.safewalk.`interface`.PingInfoInterface
import kr.co.woobi.tomorrow99.safewalk.adapter.TagAdapter
import kr.co.woobi.tomorrow99.safewalk.model.*
import kr.co.woobi.tomorrow99.safewalk.tool.calDistance
import kr.co.woobi.tomorrow99.safewalk.tool.util.ColorUtil
import org.jetbrains.anko.startActivity
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Named("locationApi")
    @Inject
    lateinit var locationApi: Retrofit

    private lateinit var naverMap: NaverMap
    private val locationPermissionCode = 1000

    @Named("server")
    @Inject
    lateinit var server: Retrofit

    var pingData = HashMap<String, DangerInformation>()
    var centerPing = Marker()

    private val locationSource by lazy {
        FusedLocationSource(this, locationPermissionCode)
    }

    private val tags = ArrayList<Tag>()

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        (supportFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment).getMapAsync(this)

        val headerLayout = LayoutInflater.from(applicationContext)
            .inflate(R.layout.layout_navigation_header, null, false)
        nv_navigation.addHeaderView(headerLayout)

        iv_navigation.setOnClickListener {
            dl_drawer.openDrawer(GravityCompat.START)
        }

        nv_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    startActivity<SettingActivity>()
                }
            }
            true
        }

        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                }

                override fun onPermissionDenied(deniedPermissions: List<String>?) {
                    ToastUtil.show(
                        applicationContext,
                        getString(R.string.map_permission_denied),
                        ToastLength.SHORT,
                        ToastType.INFO
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

        fab_danger.setOnClickListener{
            btn_search_route.visibility = View.INVISIBLE
            if (btn_declaration.visibility == View.VISIBLE) btn_declaration.visibility = View.INVISIBLE
            else btn_declaration.visibility = View.VISIBLE
        }

        btn_declaration.setOnClickListener {
            tags.clear()
            val adapter = TagAdapter(tags, this)
            adapter.setOnClickListener {
                tags.remove(it)
                adapter.notifyDataSetChanged()
                Logger.w(it.label)
            }
            val layout = LayoutInflater.from(applicationContext)
                .inflate(R.layout.layout_ping_set_dialog, null, false)

            (layout[R.id.tv_tag_add] as TextView).setOnClickListener {
                val innerDialog = MaterialAlertDialogBuilder(this@MapActivity)
                val innerLayout = LayoutInflater.from(applicationContext)
                    .inflate(R.layout.layout_tag_input, null, false)
                val label = (innerLayout[R.id.et_tag] as EditText)
                label.afterTextChanged {
                    if (it.toString().contains(" ")) {
                        label.run {
                            text = it.toString().replace(" ", "").toEditable()
                            setSelection(text.lastIndex + 1)
                        }
                    }
                }
                innerDialog.setView(innerLayout)
                innerDialog.setPositiveButton("추가") { _, _ ->
                    if (label.text.toString().isNotBlank()) {
                        tags.add(Tag(label.text.toString(), ColorUtil.randomColor))
                        adapter.notifyDataSetChanged()
                    }
                }
                innerDialog.show()
            }

            (layout[R.id.rv_tag] as RecyclerView).adapter = adapter

            (layout[R.id.iv_lens] as ConstraintLayout).setOnClickListener {
                //todo 이미지 선택과 출력

            }

            val dialog = MaterialAlertDialogBuilder(this@MapActivity)
            dialog.setView(layout)
            dialog.show()
        }

        tv_location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
                if (after == 0) {
                    btn_search_route.visibility = View.INVISIBLE
                    return;
                }

                if(btn_declaration.visibility == View.VISIBLE) {
                    centerPing.map = null
                    btn_declaration.visibility = View.INVISIBLE
                }
                btn_search_route.visibility = View.VISIBLE
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                //todo
            }

            override fun afterTextChanged(p0: Editable?) {
                //todo
            }
        })
    }

    private fun checkGpsIsOn() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ToastUtil.show(
                applicationContext,
                getString(R.string.map_need_gps),
                ToastLength.SHORT,
                ToastType.WARNING
            )

            doDelay({
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                })
            }, 1500)
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
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

        naverMap.addOnCameraChangeListener { reason, animated ->
            tv_location.text = null
            btn_search_route.visibility = View.INVISIBLE

            var center = naverMap.cameraPosition
            if (btn_declaration.visibility == View.VISIBLE) {
                centerPing.position = LatLng(center.target.latitude, center.target.longitude)
                centerPing.icon = MarkerIcons.BLACK
                centerPing.map = naverMap
            }
            else {
                centerPing.map = null
            }

            map.addOnLocationChangeListener {
                server.create(PingInfoInterface::class.java).run {
                    getPingData(
                        GetPingIn(
                            (calDistance(
                                center.target.latitude,
                                center.target.longitude,
                                map.projection.fromScreenLocation(PointF(0f, 0f)).latitude,
                                map.projection.fromScreenLocation(PointF(0f, 0f)).longitude
                            ) * 10).toString(),
                            center.target.latitude.toString(),
                            center.target.longitude.toString()
                        )
                    )
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            val setCorlor = 0.876

                            if (response.result == "success") {
                                for (ping in response.data!!) {
                                    val marker = Marker()
                                    marker.position = LatLng(
                                        ping.location["latitude"] ?: 132.0,
                                        ping.location["longitude"] ?: 37.0
                                    )

                                    marker.tag = ping.id
                                    var red = 219.0
                                    var green = 219.0

                                    if (ping.level * 5 < 2.5) {
                                        red = ping.level * 500 * setCorlor
                                    }
                                    if (ping.level * 5 > 2.5) {
                                        green = 219 - ((ping.level * 500 * setCorlor) - 219)
                                    }

                                    marker.icon = MarkerIcons.BLACK
                                    marker.iconTintColor = Color.rgb(red.toInt(), green.toInt(), 0)
                                    marker.map = naverMap
                                    
                                    marker.setOnClickListener {
                                        val TAG_INFO_DATA = pingData[it.tag.toString()]
                                        Logger.w(TAG_INFO_DATA)

                                        ////////////////////////////////////////////////////
                                        tags.clear()
                                        val adapter = TagAdapter(tags, this@MapActivity)
                                        adapter.setOnClickListener {
                                            tags.remove(it)
                                            adapter.notifyDataSetChanged()
                                            Logger.w(it.label)
                                        }
                                        val layout = LayoutInflater.from(applicationContext)
                                            .inflate(R.layout.layout_ping_info_dialog, null, false)

                                        for (tag in TAG_INFO_DATA!!.tag){
                                            tags.add(Tag(tag.toString(), ColorUtil.randomColor))
                                            adapter.notifyDataSetChanged()
                                        }

                                        (layout[R.id.rv_tag] as RecyclerView).adapter = adapter

                                        // 이미지 출력
                                        val HOST = "http://210.107.245.192:400/"
                                        val IMG_URL = HOST+"getImagePing.php?id="+TAG_INFO_DATA.id

                                        var infoImage = (layout[R.id.iv_lens] as ImageView)
                                        Logger.w(IMG_URL)

                                        Glide.with(this@MapActivity).load(IMG_URL)
                                            .apply(RequestOptions.overrideOf(infoImage.width, infoImage.height))
                                            .apply(RequestOptions.centerCropTransform())
                                            .into(layout[R.id.iv_lens] as ImageView)

                                        // 주소 출력
                                        locationApi.create(AddressInterface::class.java).run {
                                            getAddress(
                                                "${TAG_INFO_DATA.location["longitude"]},${TAG_INFO_DATA.location["latitude"]}",
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

                                                        (layout[R.id.tv_location] as TextView).text = locationData.toEditable()
                                                    }
                                                }, { throwable ->
                                                    Logger.w(throwable)
                                                    (layout[R.id.tv_location] as TextView).text =
                                                        "${
                                                            center.target.latitude.toString().substring(0..5)
                                                        },  ${
                                                            center.target.longitude.toString().substring(0..5)
                                                        }".toEditable()
                                                }, {
                                                })
                                        }

                                        // 위험 등급
                                        val SKULL = listOf(
                                            layout[R.id.iv_skull0] as ImageView,
                                            layout[R.id.iv_skull1] as ImageView,
                                            layout[R.id.iv_skull2] as ImageView,
                                            layout[R.id.iv_skull3] as ImageView,
                                            layout[R.id.iv_skull4] as ImageView
                                        )

                                        val LEVEL = (TAG_INFO_DATA.level)*5
                                        val IMG_IDX = LEVEL.toInt()
                                        for (idx in 0..IMG_IDX){
                                            if(idx == 5 || IMG_IDX == 0) break
                                            SKULL[idx].setImageResource(R.drawable.skull3)
                                        }
                                        if ((LEVEL - IMG_IDX) >= 0.5 ) SKULL[IMG_IDX].setImageResource(R.drawable.skull2)
                                        else if(IMG_IDX != 5)SKULL[IMG_IDX].setImageResource(R.drawable.skull1)

                                        (layout[R.id.tv_danger_level_voted] as TextView).text = LEVEL.toString()

                                        // 유용성
                                        val TRUE_CNT = TAG_INFO_DATA.useful["true"]
                                        val FALSE_CNT = TAG_INFO_DATA.useful["false"]

                                        if (TRUE_CNT!! == FALSE_CNT!!){
                                            (layout[R.id.ll_border_false] as LinearLayout).background.setTint(R.color.colorLightRed)
                                            (layout[R.id.ll_border_true] as LinearLayout).background.setTint(R.color.colorSky)
                                        }
                                        else if(TRUE_CNT > FALSE_CNT){
                                            (layout[R.id.ll_border_usefull] as LinearLayout).background.setTint(R.color.colorSky)
                                            (layout[R.id.ll_border_false] as LinearLayout).background.setTint(R.color.colorLightRed)
                                            (layout[R.id.ll_border_true] as LinearLayout).background = null
                                        }
                                        else {
                                            (layout[R.id.ll_border_usefull] as LinearLayout).background.setTint(R.color.colorLightRed)
                                            (layout[R.id.ll_border_true] as LinearLayout).background.setTint(R.color.colorSky)
                                            (layout[R.id.ll_border_false] as LinearLayout).background = null
                                        }
                                        (layout[R.id.tv_good_count] as TextView).text = TRUE_CNT.toString()
                                        (layout[R.id.tv_bad_count] as TextView).text = FALSE_CNT.toString()

                                        val dialog = MaterialAlertDialogBuilder(this@MapActivity)
                                        dialog.setView(layout)
                                        dialog.show()

                                        false
                                        ////////////////////////////////////////////////////
                                    }

                                    pingData.put(ping.id.toString(), ping)

                                    //markerList.put(data.id.toString(),marker)
                                }
                            }
                        }, { throwable ->
                            Logger.w(throwable)
                            Toast.makeText(this@MapActivity, "${throwable.message}.", Toast.LENGTH_SHORT).show()
                        }, {
                        })
                }
            }
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

                                tv_location.hint = locationData.toEditable()
                            }
                        }, { throwable ->
                            Logger.w(throwable)
                            tv_location.hint =
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
