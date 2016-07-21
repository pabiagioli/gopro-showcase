package com.aajtech.mobile.goproshowcase.dto

/**
 * Created by pablo.biagioli on 7/20/16.
 */
data class GoProStatusResponse(val status: Map<Int, Any>, val settings: Map<Int, Any>) {

    /**
     * Returns the battery level percentage
     * The Battery Level is between [0..3],
     * The value is rounded up from its first digit from the left.
     */
    fun batteryLvlPercentage(): Long {
        val batLvlK = GoProConstants.status[GoProConstants.batteryLevel]!!
        val batLvl = status[batLvlK]
        val result = when (batLvl) {
            is String -> Math.round(batLvl.toDouble() * 10 / 3) * 10
            else -> Math.round(batLvl.toString().toDouble() * 10 / 3) * 10
        }
        return result;
    }
}

data class GoProStatusDTO(val propId: Int, val propName: String, val propValue: Any)

/**
 * Builder Extension method to fill a RecyclerView with Status key,values
 */
fun GoProStatusResponse.buildViewHolderData(): MutableList<GoProStatusDTO> {
    val result = emptyList<GoProStatusDTO>().toMutableList()
    for ((property, id) in GoProConstants.status) {
        val propValue = this.status[id]
        if (propValue != null)
            result.add(GoProStatusDTO(id, property, propValue))
        //println("$property = $propValue")
    }
    return result
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