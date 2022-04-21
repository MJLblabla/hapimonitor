package com.hapi.hapiapm;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.hapi.blockmonitor.BlockTranceManager;
import com.hapi.blockmonitor.MonitorConfig;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        BlockTranceManager.INSTANCE.init(this, new MonitorConfig());

        BlockTranceManager.INSTANCE.getMBlockTracer().start();
    }
}
