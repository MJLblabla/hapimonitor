package com.hapi.monitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class IssuesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issues)
        val issure = intent.getStringExtra("issure")
        findViewById<TextView>(R.id.tvCOntent).text = issure;
    }
}