package org.boblloyd.gradleprojectreport

import org.gradle.api.Project
import org.gradle.api.Plugin
// import org.gradle.api.DefaultTask;
// import org.gradle.api.provider.Property;
// import org.gradle.api.tasks.Input;
// import org.gradle.api.tasks.TaskAction;

class ProjectReport implements Plugin<Project> {
    void apply(Project project) {
        // Add the 'projectReport' extension object
        def extension = project.extensions.create('projectReport', ProjectReportExtension)
        // Default the output directory to the project's build output, but this can be overridden
        extension.output = "${project.buildDir}/reports/projectReport.md"
        // Add a task that uses configuration from the extension object
        project.task("projectReport"){
            doLast(){
                println "Version: ${project.version}"
                println "Group: ${project.group}"
                println "Is Show Dependencies: ${extension.renderDependencies.get()}"
                println "Report Output Directory: ${extension.output.get()}"
            }
        }
    }
}