package com.hapi.hapiapm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hapi.hapiapm.R
import com.hapi.testmoudle.A

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Ts.ts()

        findViewById<Button>(R.id.bt).setOnClickListener {
           // Ts.tss()
            A.aVoid()
        }
    }
}