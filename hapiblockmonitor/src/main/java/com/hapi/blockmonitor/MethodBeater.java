package com.hapi.blockmonitor;


import com.hapi.monitor.Beat;

import java.util.LinkedList;

/**
 * 打点器
 */
public class MethodBeater {

    private final String Tag = "MethodBeatMonitor";
    /**
     * 过滤短耗时
     */
    private static final int minCostFilter = 5;
    //方法深度
    private static int methodDeep = 0;
    //最大深度
    private static final int maxDeep = 100000;
    private static final int issuesTop = 30;
    private static final LinkedList<Beat> beats = new LinkedList<Beat>();
    private static final BeatAdapter mBeatAdapter = new BeatAdapterImpl();

    public static void dispatchMsgStart() {
        methodDeep = 0;
        beats.clear();
    }

    public static boolean checkDeep() {

        if (!mBeatAdapter.isAddBeatAble()) {
            return false;
        }
        if (Thread.currentThread().getId() != mBeatAdapter.getMainThreadId()) {
            return false;
        }
        if (!mBeatAdapter.isMainStart()) {
            return false;
        }
        return methodDeep < maxDeep;
    }

    /**
     * 方法结束 打点
     *
     * @param beat
     */
    public static void addBeat(Beat beat) {
        if (!mBeatAdapter.isAddBeatAble()) {
            return;
        }
        methodDeep++;
        beats.addFirst(beat);
    }

    public static int getTime() {
        return mBeatAdapter.getCurrentTime();
    }

    public static int getMinCostFilter() {
        return minCostFilter;
    }

    /**
     * 取出打点记录 回调上报
     *
     * @param msg
     */
    public static void issue(String msg, int maxTop) {
        mBeatAdapter.issues(beats, maxTop, msg);
    }
}
