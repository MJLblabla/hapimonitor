package com.hapi.blockmonitor.func

import android.os.Looper
import android.util.Log
import android.util.Printer
import com.hapi.aop.util.ReflectUtils
import com.hapi.blockmonitor.MethodBeater
import java.util.*

/**
 * 监听主线程 loop的一次开始
 */
object LooperMonitor {

    private val TAG = "TAG"
    private val looper by lazy { Looper.getMainLooper() }

    private var isInt = false
    var isStart = false
        private set

    private var mLoopListeners = LinkedList<LoopListener>()

    fun registerLoopListener(loopListener: LoopListener, register: Boolean) {
        if (register) {
            mLoopListeners.add(loopListener)
            if (mLoopListeners.size > 0 && !isStart) {
                start()
            }
        } else {
            mLoopListeners.remove(loopListener)
            if (mLoopListeners.size == 0 && isStart) {
                stop()
            }
        }
    }

    interface LoopListener {
        fun dispatchMsgStart()
        fun dispatchMsgStop()
    }

    class LooperPrinter(var origin: Printer?) :
        Printer {
        var isHasChecked = false
        var isValid = false
        override fun println(x: String) {
            origin?.println(x)
            if (!isHasChecked) {
                isValid = x[0] == '>' || x[0] == '<'
                isHasChecked = true
            }
            if (isValid) {
                dispatch(x[0] == '>')
            }
        }
    }

    private fun dispatch(isBegin: Boolean) {
        if (!isStart) {
            return
        }
        if (isBegin) {
            MethodBeater.dispatchMsgStart()
            mLoopListeners.forEach {
                it.dispatchMsgStart()
            }
        } else {
            mLoopListeners.forEach {
                it.dispatchMsgStop()
            }
            LoopTimer.reset()
        }
    }

    private var isReflectLoggingError = false

    private fun init() {
        isInt = true
        var originPrinter: Printer? = null
        try {
            if (!isReflectLoggingError) {
                originPrinter = ReflectUtils.get(looper.javaClass, "mLogging", looper)
            }
        } catch (e: Exception) {
            isReflectLoggingError = true
            Log.e(TAG, "[resetPrinter] %s", e)
        }
        looper.setMessageLogging(LooperPrinter(originPrinter));
    }

    private fun start() {
        if (!isInt) {
            init()
        }
        isStart = true
    }

    private fun stop() {
        isStart = false
    }
}