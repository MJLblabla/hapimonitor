package com.hapi.hapiplugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class HapiAppMonitorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("hapi", Hapi)
        def android = project.extensions.getByType(BaseExtension)
        android.registerTransform(new HAPITransForm(project), Collections.EMPTY_LIST)
    }
}