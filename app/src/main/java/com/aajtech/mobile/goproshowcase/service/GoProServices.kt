package com.aajtech.mobile.goproshowcase.service

import com.aajtech.mobile.goproshowcase.dto.GoProStatusResponse
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Created by pablo.biagioli on 7/20/16.
 */
val okHttpClient: OkHttpClient by lazy {
    OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()!!
}

val moshi: Moshi by lazy { Moshi.Builder().build()!! }

val retrofit: Retrofit by lazy {
    Retrofit.Builder()
            .baseUrl("http://10.5.5.9/gp/gpControl/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()!!
}

val GoProFixedIPAddress = "10.5.5.9"
val GoProMACaddress = "F4DD9E0DE0F7"

fun sendWoL(ip: String = GoProFixedIPAddress, mac: String = GoProMACaddress) {
    val macStr = mac
    val ipStr = ip

    try {
        val macStr1 = MagicPacket.cleanMac(macStr)
        println("Sending to: " + macStr)
        MagicPacket.send(macStr1, ipStr)
    } catch(e: IllegalArgumentException) {
        e.printStackTrace()
    } catch(e: Exception) {
        System.out.println("Failed to send Wake-on-LAN packet:" + e.message)
    }
}

interface GoProInfoService {

    /**
     * get Status codes
     */
    @GET("status")
    fun status(): Call<GoProStatusResponse>

    @GET("command/system/sleep")
    fun powerOff(): Call<Any>

    @GET("command/wireless/ap/ssid")
    fun nameCmd(@Query("ssid") ssid: String, @Query("pw") pass: String): Call<ResponseBody>

}

interface GoProAnalyticsService {
    /**
     * Acquire the analytics file as an octet-stream.
     */
    @GET("analytics/get")
    fun analytics(): Call<ResponseBody>
}

interface GoProPrimaryModeService {
    @GET("command/mode")
    fun setPrimaryMode(@Query("p") mode: Int): Call<ResponseBody>
}

interface GoProSecondaryModeService {
    @GET("command/sub_mode")
    fun setSubMode(@Query("mode") mode: Int, @Query("sub_mode") subMode: Int): Call<ResponseBody>
}

interface GoProShutterService {
    /**
     * Trigger command
     */
    @GET("command/shutter")
    fun shutterToggle(@Query("p") toggle: Int): Call<ResponseBody>
}