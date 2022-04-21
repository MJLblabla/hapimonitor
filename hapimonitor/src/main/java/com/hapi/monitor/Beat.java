package com.hapi.monitor;


import java.io.Serializable;

//方法打点
public class Beat implements Serializable {
    //耗时
    public int cost;
    //方法标记签名
    public String sign="";
    //方法ID
    public int id;
    public Beat(){}
    public Beat(int cost, String sign) {
        this.cost = cost;
        this.sign = sign;
    }

}
