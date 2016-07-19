package com.aajtech.mobile.goproshowcase

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Created by pablo.biagioli on 7/19/16.
 */
class GoProCommandsUnitTest {

    @Test
    fun testGoProPair(){
        val ssid = "aajGoPro"
        val pass = "44JT3ch01"
        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://10.5.5.9/gp/gpControl/")
                .client(okHttpClient)
                .build()
        val service = retrofit.create(GoProPairService::class.java)
        val pairReq = service.pairCommand(ssid,pass)
        val response = pairReq.execute()
        println(response.body().string())
    }
}

interface GoProPairService {
    @GET("command/wireless/ap/ssid")
    fun pairCommand(@Query("ssid")ssid:String, @Query("pw")pass:String): Call<ResponseBody>

}