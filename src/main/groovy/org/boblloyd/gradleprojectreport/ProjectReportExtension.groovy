package org.boblloyd.gradleprojectreport

import org.gradle.api.provider.Property;

abstract class ProjectReportExtension {
    abstract Property<String> getOutput()
    abstract Property<Boolean> getRenderDependencies()

    ProjectReportExtension(){
        renderDependencies.convention(false)
    }
}