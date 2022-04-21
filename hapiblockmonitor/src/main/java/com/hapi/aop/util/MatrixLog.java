
package com.hapi.aop.util;

/**
 * Created by zhangshaowen on 17/5/17.
 */

public class MatrixLog {
    private static MatrixLogImp debugLog = new MatrixLogImp() {

        @Override
        public void v(final String tag, final String format, final Object... params) {
            String log = (params == null || params.length == 0) ? format : String.format(format, params);
            android.util.Log.v(tag, log);
        }

        @Override
        public void i(final String tag, final String format, final Object... params) {
            String log = (params == null || params.length == 0) ? format : String.format(format, params);
            android.util.Log.i(tag, log);

        }

        @Override
        public void d(final String tag, final String format, final Object... params) {
            String log = (params == null || params.length == 0) ? format : String.format(format, params);
            android.util.Log.d(tag, log);
        }

        @Override
        public void w(final String tag, final String format, final Object... params) {
            String log = (params == null || params.length == 0) ? format : String.format(format, params);
            android.util.Log.w(tag, log);
        }

        @Override
        public void e(final String tag, final String format, final Object... params) {
            String log = (params == null || params.length == 0) ? format : String.format(format, params);
            android.util.Log.e(tag, log);
        }

        @Override
        public void printErrStackTrace(String tag, Throwable tr, String format, Object... params) {
            String log = (params == null || params.length == 0) ? format : String.format(format, params);
            if (log == null) {
                log = "";
            }
            log += "  " + android.util.Log.getStackTraceString(tr);
            android.util.Log.e(tag, log);
        }
    };

    private static MatrixLogImp matrixLogImp = debugLog;

    private MatrixLog() {
    }

    public static void setMatrixLogImp(MatrixLogImp imp) {
        matrixLogImp = imp;
    }

    public static MatrixLogImp getImpl() {
        return matrixLogImp;
    }

    public static void v(final String tag, final String msg, final Object... obj) {
        if (matrixLogImp != null) {
            matrixLogImp.v(tag, msg, obj);
        }
    }

    public static void e(final String tag, final String msg, final Object... obj) {
        if (matrixLogImp != null) {
            matrixLogImp.e(tag, msg, obj);
        }
    }

    public static void w(final String tag, final String msg, final Object... obj) {
        if (matrixLogImp != null) {
            matrixLogImp.w(tag, msg, obj);
        }
    }

    public static void i(final String tag, final String msg, final Object... obj) {
        if (matrixLogImp != null) {
            matrixLogImp.i(tag, msg, obj);
        }
    }

    public static void d(final String tag, final String msg, final Object... obj) {
        if (matrixLogImp != null) {
            matrixLogImp.d(tag, msg, obj);
        }
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        if (matrixLogImp != null) {
            matrixLogImp.printErrStackTrace(tag, tr, format, obj);
        }
    }

    public interface MatrixLogImp {

        void v(final String tag, final String msg, final Object... obj);

        void i(final String tag, final String msg, final Object... obj);

        void w(final String tag, final String msg, final Object... obj);

        void d(final String tag, final String msg, final Object... obj);

        void e(final String tag, final String msg, final Object... obj);

        void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj);

    }
}
