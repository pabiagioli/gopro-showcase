package com.aajtech.mobile.goproshowcase

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.aajtech.mobile.goproshowcase.service.retrofit
import com.aajtech.mobile.goproshowcase.service.wifiNetwork
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pairSubmit.setOnClickListener {
            startActivity(Intent(this, GoProCmdsActivity::class.java))
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE), 200)
            return
        }

        initNetworkRouting(this)

    }

    fun initNetworkRouting(ctx: Context){
        val conMgr = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nets = conMgr.allNetworks
        for (net in nets){
            val info = conMgr.getNetworkInfo(net)
            //println(""+info.typeName+": "+info.type+" ("+info.isAvailable+")")
            when(info.type){
                ConnectivityManager.TYPE_WIFI->{
                    wifiNetwork = net
                    retrofit.toString()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiNetwork = null
    }

}
