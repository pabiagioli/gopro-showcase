package com.aajtech.mobile.goproshowcase

import android.app.Application
import android.content.Context
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * Created by pablo.biagioli on 8/3/16.
 */
class GoProShowcaseApplication: Application(){

    lateinit var refWatcher: RefWatcher

    fun getRefWatcher(context: Context): RefWatcher {
        val application = context.getApplicationContext() as GoProShowcaseApplication
        return application.refWatcher
    }

    override fun onCreate() {
        super.onCreate()
        refWatcher = LeakCanary.install(this)
    }
}