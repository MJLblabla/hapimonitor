package com.hapi.monitor;

import android.content.Intent

interface NotificationAble {

    fun getTittle():String
    fun getContent():String
    fun getIntent(): Intent?
}