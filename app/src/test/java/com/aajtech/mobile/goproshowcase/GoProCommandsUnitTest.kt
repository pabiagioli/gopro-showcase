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

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()!!
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.5.5.9/gp/gpControl/")
            .client(okHttpClient)
            .build()!!
    }

    @Test
    fun testGoProInfo(){
        val ssid = "aajGoPro"
        val pass = "44JT3ch01"
        val service = retrofit.create(GoProInfoService::class.java)

        //Requests
        val nameCmdReq = service.nameCmd(ssid,pass)
        val statusReq = service.status()

        //Responses
        val responsePair = nameCmdReq.execute()
        val responseStatus = statusReq.execute()


        assert(responsePair.isSuccessful)
        println(responsePair.body().string())

        assert(responseStatus.isSuccessful)
        println(responseStatus.body().string())
    }

    @Test
    fun testGetAnalytics(){
        val service = retrofit.create(GoProAnalyticsService::class.java)
        assert(service.analytics().execute().isSuccessful)
        /*.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                assert(response != null)
                if(response != null)
                    assert(response.isSuccessful)
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                assert(false)
            }
        })*/
    }
}



interface GoProInfoService {

    @GET("status")
    fun status():Call<ResponseBody>

    @GET("command/wireless/ap/ssid")
    fun nameCmd(@Query("ssid")ssid:String, @Query("pw")pass:String): Call<ResponseBody>

}

interface GoProAnalyticsService {
    /**
     * Acquire the analytics file as an octet-stream.
     */
    @GET("analytics/get")
    fun analytics():Call<ResponseBody>
}