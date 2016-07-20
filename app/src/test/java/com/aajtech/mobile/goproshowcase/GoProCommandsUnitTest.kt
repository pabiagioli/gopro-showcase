package com.aajtech.mobile.goproshowcase

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    val moshi: Moshi by lazy { Moshi.Builder().build()!! }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("http://10.5.5.9/gp/gpControl/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()!!
    }

    @Test
    fun testWoL(){
        val macStr = "F4DD9E0DE0F7"
        val ipStr = "10.5.5.9"
        try	{
            val macStr1 = MagicPacket.cleanMac(macStr)
            println("Sending to: "+macStr)
            MagicPacket.send(macStr1, ipStr)
        }
        catch(e:IllegalArgumentException) {
            e.printStackTrace()
        }catch(e:Exception) {
            System.out.println("Failed to send Wake-on-LAN packet:" + e.message)
        }
    }

    /**
     * "info": {
     * "model_number": 16,
     * "model_name": "HERO4 Session",
     * "firmware_version": "HX1.01.01.00",
     * "serial_number": "C3141324949509",
     * "board_type": "0x07",
     * "ap_mac": "F4DD9E0DE0F7",
     * "ap_ssid": "aajGoPro",
     * "ap_has_default_credentials": "0",
     * "git_sha1": "e4c3eabd3ab8d3f53067e986458ef04652222fd4"
     * }
     */
    @Test()
    fun testGoProInfo(){
        val ssid = "aajGoPro"
        val pass = "44JT3ch01"

        testWoL()

        val service = retrofit.create(GoProInfoService::class.java)

        //Requests
        //val nameCmdReq = service.nameCmd(ssid,pass)
        val statusReq = service.status()

        //Responses
        //val responsePair = nameCmdReq.execute()
        val responseStatus = statusReq.execute()


        //assert(responsePair.isSuccessful)
        //println(responsePair.body().string())

        assert(responseStatus.isSuccessful)
        println(responseStatus.body().toString())
        val batLvlK = GoProConstants.status[GoProConstants.batteryLevel]!!
        val batLvl = responseStatus.body()["status"]!![batLvlK]
        println("Battery Level $batLvlK : $batLvl")
    }

    @Test
    fun testGetAnalytics(){

        testWoL()

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

object GoProConstants {

    /**
     * Go Pro Hero 4 Session's Internal Battery Level
     */
    val batteryLevel = "InternalBatteryLevel"

    val status: Map<String, Int>
        get() = mapOf(
                "InternalBatteryPresent" to 1,
                "InternalBatteryLevel" to 2,
                "ExternalBatteryPresent" to 3,
                "ExternalBatteryLevel" to 4,
                "CurrentTemperature" to 5,
                "SystemHot" to 6,
                "SystemBusy" to 8,
                "QuickCaptureActive" to 9,
                "EncodingActive" to 10,
                "LcdLockActive" to 11,
                "CameraLocateActive" to 45,
                "Mode" to 43,
                "SubMode" to 44,
                "Xmode" to 12,
                "VideoProgressCounter" to 13,
                "VideoProtuneDefault" to 46,
                "PhotoProtuneDefault" to 47,
                "MultiShotProtuneDefault" to 48,
                "MultiShotCountDown" to 49,
                "BroadcastProgressCounter" to 14,
                "BroadcastViewersCount" to 15,
                "BroadcastBstatus" to 16,
                "WirelessEnable" to 17,
                "WirelessPairState" to 19,
                "WirelessPairType" to 20,
                "WirelessScanState" to 22,
                "WirelessScanTime" to 23,
                "WirelessScanCurrentTime" to 18,
                "WirelessPairing" to 28,
                "WirelessRemoveControlVersion" to 26,
                "WirelessRemoveControlConnected" to 27,
                "WirelessAppCount" to 31,
                "WirelessProvisionStatus" to 24,
                "WirelessRssiBars" to 25,
                "WirelessWlanSsid" to 29,
                "WirelessApSsid" to 30,
                "SdStatus" to 33,
                "RemainingPhotos" to 34,
                "RemainingVideoTime" to 35,
                "NumGroupPhotos" to 36,
                "NumGroupVideos" to 37,
                "NumTotalPhotos" to 38,
                "NumTotalVideos" to 39,
                "DateTime" to 40,
                "FWUpdateOtaStatus" to 41,
                "FWUpdateDownloadCancelRequestPending" to 42
        )
}

interface GoProInfoService {

    /**
     * get Status codes
     */
    @GET("status")
    fun status():Call<Map<String,Map<Int,Any>>>

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

