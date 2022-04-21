package com.hapi.blockmonitor

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.hapi.blockmonitor.trans.BlockTracer
import com.hapi.blockmonitor.trans.FpsTracer
import com.hapi.blockmonitor.trans.TraversalTracer
import com.hapi.monitor.*


/**
 * 插件开启，配置入口
 */
@SuppressLint("StaticFieldLeak")
object BlockTranceManager {

    var context: Context? = null
        private set

    val mBlockTracer = BlockTracer()
    val mFpsTracer = FpsTracer()
    val mTraversalTracer = TraversalTracer()

    var mIssuesCallBack = DefaultIssuesCallBack()

    var mMonitorConfig = MonitorConfig()
        private set

    fun init(application: Application, config: MonitorConfig = MonitorConfig()) {
        context = application
        this.mMonitorConfig = config
        ActivityCollection.init(application)
    }

}