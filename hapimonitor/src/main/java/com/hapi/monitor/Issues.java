package com.hapi.monitor;

import java.io.Serializable;
import java.util.List;

public class Issues implements Serializable {
    public Issues(){
    }
    /**
     * 提示信息
     */
    public String msg;
    
    /**
     * 可用内存
     */
    public  long availMemory ;
    /**
     * 最大内存
     */
    public  long totalMemory ;//= DeviceUtil.getTotalMemory(HapiPlugin.context)

    /**
     * cup使用
     */
    public  double cpuRate ;//= DeviceUtil.getAppCpuRate()

    /**
     * 页面名称
     */
    public String foregroundPageName;
    /**
     * 函数调用链
     */
    public List<Beat> methodBeats;


}
