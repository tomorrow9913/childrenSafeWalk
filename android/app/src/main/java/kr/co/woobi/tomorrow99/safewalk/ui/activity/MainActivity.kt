package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kr.co.woobi.tomorrow99.safewalk.R

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (supportFragmentManager.findFragmentById(R.id.fcv_map) as? MapFragment?)?.getMapAsync(this)

        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage()
            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .check()

    }

    override fun onMapReady(map: NaverMap) {
        map.uiSettings.isLogoClickEnabled = false
    }

}
