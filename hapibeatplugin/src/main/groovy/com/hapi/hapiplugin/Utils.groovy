package com.hapi.hapiplugin

class Utils {

    static def jarList = []
    //过滤掉一些生成的类
    static boolean check(File file) {
        if (file.isDirectory()) {
            return false
        }
        def filePath = file.path

        return checkStr(filePath)
    }
    //过滤掉一些生成的类
    static boolean checkStr(String filePath) {

        return !filePath.contains('R.class') &&
                !filePath.startsWith('kotlinx/') &&
                !filePath.startsWith('kotlin/') &&
                !filePath.contains('com/hapi/aop/') &&
                !filePath.contains('androidx/lifecycle/') &&
                filePath.endsWith(".class") &&
                !filePath.contains('BuildConfig.class')
    }
}