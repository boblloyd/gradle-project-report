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
        extension.output = "${project.buildDir}/reports/"
        // Add a task that uses configuration from the extension object
        project.tasks.register('projectReport', ProjectReportTask){
            projectName = project.name
            projectDescription = project.description ?: ""
            projectGroup = project.group ?: ""
            // We only need to set the configurations if we are actually setting the renderDependencies
            //  value to true.  Otherwise, we don't set the project.configurations to evaluate
            if(extension.renderDependencies.get()){
                project.configurations.each{config ->
                    if(config.canBeResolved){
                        configurations.put("${config.name}", getDependenciesForConfiguration(config))
                    }
                }
            }
            reportPath = new File(extension.output.get(), getReportName().get())
        }
    }

    private def getDependenciesForConfiguration(def config){
        def depList = []
        config.dependencies.each{ dependency ->
            config.files(dependency).each{
                String group = dependency.group ?: 'file'
                String name = dependency.name
                String version = dependency.version ?: ''
                String file = it.name
                if(name == 'unspecified'){
                    name = ''
                }
                String gav = "${group}:${name}:${version}"
                if(gav == "file::"){
                    file = it.path
                }
                depList.add("${gav} - ${file}")
            }
        }
        return depList
    }
}