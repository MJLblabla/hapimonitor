package com.hapi.monitor

import com.hapi.monitor.Issues


interface IssuesCallBack {

    fun onIssues(issues: Issues)
}
