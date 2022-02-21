package org.boblloyd.gradleprojectreport

import java.io.File

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

abstract class ProjectReportTask extends DefaultTask{
    @Input
    abstract public Property<String> getProjectName();
    @Input
    abstract public Property<String> getProjectDescription();
    @Input
    abstract public Property<String> getProjectGroup();
    @Input
    abstract public MapProperty<String, ArrayList> getConfigurations();
    @Input
    abstract public Property<String> getOutputDir()

    @Internal
    final Provider<String> reportName = projectName.map { it + '.md' }

    @OutputFile
    abstract public RegularFileProperty getReportPath()

    @TaskAction
    public void projectReport(){
        getConfigurations().finalizeValueOnRead()
        ProjectReportModel model = new ProjectReportModel()

        model.name = getProjectName().get()
        model.description = getProjectDescription().get()
        model.group = getProjectGroup().get()
        model.configurations = getConfigurations().get()

        new ProjectReportWriter().writeReport(model, reportPath.get().asFile)
    }

}