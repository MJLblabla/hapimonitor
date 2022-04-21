package com.hapi.blockmonitor.trans

import android.util.Log
import com.hapi.blockmonitor.BlockTranceManager
import com.hapi.blockmonitor.func.LoopTimer
import com.hapi.blockmonitor.MethodBeater

/**
 * 卡顿检查
 */
class BlockTracer : ITracer() {

    private val TAG = "BlockTracer"
    private var startTime = 0

    override fun loopStart() {
        super.loopStart()
        startTime = LoopTimer.time
    }

    override fun loopStop() {
        super.loopStop()
        val endTime = LoopTimer.time
        val timeCost = endTime - startTime

        if (timeCost > BlockTranceManager.mMonitorConfig.blockCostIssue) {
            Log.d("MethodBeater","主线程卡顿 $timeCost")
            MethodBeater.issue("主线程卡顿 $timeCost",timeCost)
        }
    }
}