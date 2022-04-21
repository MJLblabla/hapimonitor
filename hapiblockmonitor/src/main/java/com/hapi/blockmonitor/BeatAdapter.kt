package com.hapi.blockmonitor

import android.os.Looper
import android.util.Log
import com.hapi.aop.util.DeviceUtil
import com.hapi.blockmonitor.func.LoopTimer
import com.hapi.blockmonitor.func.LooperMonitor
import com.hapi.monitor.ActivityCollection
import com.hapi.monitor.Beat
import com.hapi.monitor.Issues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

interface BeatAdapter {
    fun isMainStart(): Boolean
    fun getMainThreadId(): Long
    fun getCurrentTime(): Int
    fun issues(beats: MutableList<Beat>, maxTop: Int, msg: String)
    fun isAddBeatAble(): Boolean
}

internal class BeatAdapterImpl : BeatAdapter {
    override fun getCurrentTime(): Int {
        return LoopTimer.time
    }

    override fun getMainThreadId(): Long {
        return Looper.getMainLooper().thread.id
    }

    override fun issues(bt: MutableList<Beat>, maxTop: Int, msg: String) {
        val beatsClone = LinkedList<Beat>()
        beatsClone.addAll(bt)
        GlobalScope.launch(Dispatchers.Main) {
            val res = async<LinkedList<Beat>?> {
                val beatTemp =
                    LinkedList<Beat>()
                if (beatsClone.isEmpty()) {
                    Log.d("mjl", "beats is empty")
                } else {
                    var maxCost = 0
                    beatsClone.forEach {
                        if (it.cost > maxCost) {
                            maxCost = it.cost
                        }
                    }
                    if (
                        beatsClone.isEmpty()
                        || maxCost < maxTop * 0.5
                    ) {
                        Log.d("mjl", "maxCost" + maxCost)
                    } else {
                        beatTemp.addAll(beatsClone)
                    }
                }
                beatTemp
            }

            res.await()?.let { beat ->
                if (beat.isEmpty()) {
                    return@let
                }
                BlockTranceManager.mIssuesCallBack.let {
                    val issues = Issues()
                    issues.msg = msg
                    issues.availMemory = DeviceUtil.getMemFree(BlockTranceManager.context)
                    issues.totalMemory =
                        DeviceUtil.getTotalMemory(BlockTranceManager.context)
                    issues.cpuRate = DeviceUtil.getAppCpuRate()
                    issues.foregroundPageName =
                        ActivityCollection.currentActivity?.localClassName
                    issues.methodBeats = beat
                    it.onIssues(issues)
                }
            }
        }
    }

    override fun isAddBeatAble(): Boolean {
        return LooperMonitor.isStart
    }

    override fun isMainStart(): Boolean {
        return LoopTimer.isStart
    }

}