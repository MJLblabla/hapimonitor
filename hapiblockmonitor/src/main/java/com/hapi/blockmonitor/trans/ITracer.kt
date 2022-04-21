package com.hapi.blockmonitor.trans

import com.hapi.blockmonitor.func.LoopTimer
import com.hapi.blockmonitor.func.LooperMonitor

open class ITracer(private var timerNeed: Boolean = true, private var loopNeed: Boolean = true) {

    protected var isStart = false

    protected val mLoopListener = object:LooperMonitor.LoopListener {
        override fun dispatchMsgStart() {
            if (isStart) {
                loopStart()
            }
        }

        override fun dispatchMsgStop() {
            if (isStart) {
                loopStop()
            }
        }
    }

    protected open fun loopStart() {

    }

    protected open fun loopStop() {

    }

    open fun start() {
        isStart = true
        if (timerNeed) {
            LoopTimer.addTimerObsever()
        }
        if (loopNeed) {
            LooperMonitor.registerLoopListener(mLoopListener, true)
        }
    }

    open fun stop() {
        if (timerNeed) {
            LoopTimer.removeTimerObsever()
        }
        isStart = false
        if (loopNeed) {
            LooperMonitor.registerLoopListener(mLoopListener, false)
        }
    }
}