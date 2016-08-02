package com.aajtech.mobile.goproshowcase.dto

/**
 * Created by pablo.biagioli on 8/2/16.
 */
enum class GoProPrimaryModes(val mode: Int) {
    VIDEO(0),
    PHOTO(1),
    MULTI_SHOT(2)
}

enum class GoProSecondaryModes(val mode: Int, val subMode: Int) {
    VIDEO_VIDEO(0, 0),
    TIME_LAPSE_VIDEO(0, 1),
    VIDEO_PLUS_PHOTO(0, 2),
    LOOPING_VIDEO(0, 3),
    SINGLE_PHOTO(1, 0),
    CONTINUOUS_PHOTO(1, 1),
    NIGHT_PHOTO(1, 2),
    BURST_MULTI_SHOT(2, 0),
    TIME_LAPSE_MULTI_SHOT(2, 1),
    NIGHT_LAPSE_MULTI_SHOT(2, 2)
}

enum class GoProShutterModes(val mode: Int) {
    TRIGGER_SHUTTER(1),
    STOP_SHUTTER(0)
}

enum class GoProStreamingModes(val param1: String, val command: String) {
    START_STREAMING("gpStream", "restart"),
    STOP_STREAMING("gpStream", "stop")
}