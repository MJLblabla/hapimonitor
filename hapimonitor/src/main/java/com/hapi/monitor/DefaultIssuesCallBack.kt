package com.hapi.monitor

import android.content.Intent
import com.hapi.monitor.Issues
import com.hapi.monitor.IssuesCallBack
import java.util.*
import kotlin.Comparator

class DefaultIssuesCallBack: IssuesCallBack {
    override fun onIssues(issure: Issues) {
        NotificationHelper.send(object : NotificationAble {
            override fun getTittle(): String {
                return "老铁，你的代码有点卡->>>"
            }

            override fun getContent(): String {
                return issure.msg
            }

            override fun getIntent(): Intent? {

                val msg = "msg  ${issure.msg}  " +
                        "\n availMemory ${issure.availMemory} " +
                        " \n totalMemory ${issure.totalMemory} " +
                        "\n foregroundPageName  ${issure.foregroundPageName}" +
                        "  cpuRate ${issure.cpuRate} \n\n"
                val sb = StringBuffer()
                sb.append(msg+"  调用顺序：")


                issure.methodBeats?.forEachIndexed { index, methodBeat ->
                    sb.append(" ${index}  ${methodBeat.sign}  cost ${methodBeat.cost} \n")

                }
                sb.append("耗时排序 : '\n\n\n")

                issure.methodBeats?.let {
                    Collections.sort<Beat>(it, object : Comparator<Beat> {
                        override fun compare(o1: Beat, o2: Beat): Int {
                            return  o2.cost-o1.cost
                        }
                    })
                }
                issure.methodBeats?.forEachIndexed { index, methodBeat ->
                    sb.append(" ${methodBeat.sign}  cost ${methodBeat.cost} \n")
                }
                val intent = if (ActivityCollection.currentActivity != null) {
                    Intent(ActivityCollection.currentActivity, IssuesActivity::class.java)
                } else {
                    Intent(ActivityCollection.appContext, IssuesActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                intent.putExtra("issure",sb.toString())
                return intent
            }
        })
    }
}