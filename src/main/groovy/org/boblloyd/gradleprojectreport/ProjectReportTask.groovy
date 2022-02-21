package org.boblloyd.gradleprojectreport

import java.io.File

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.OutputDirectory

import org.gradle.api.tasks.TaskAction;
import org.gradle.api.artifacts.ConfigurationContainer;

abstract class ProjectReportTask extends DefaultTask{
    @Input
    abstract public Property<String> getProjectName();
    @Input
    abstract public Property<String> getProjectVersion();
    @Input
    abstract public Property<String> getProjectGroup();
    // @Input
    // abstract public Property<ConfigurationContainer> getProjectConfigurations();

    @OutputDirectory
    abstract DirectoryProperty getOutputDir()

    @Internal
    // final Property<String> reportName = projectName.map{it + '.md'}
    final Provider<String> reportName = projectName.map { it + '.md' }

    // @Internal
    // @OutputFile
    // final private RegularFileProperty outputFile = File("${outputDir.map{it}}", "${reportName.map{it}}")

    @TaskAction
    public void projectReport(){
        println("Project Name    : ${getProjectName().get()}")
        println("Project Version : ${getProjectVersion().get()}")
        println("Project Group   : ${getProjectGroup().get()}")
        // println("Project Configurations: ")
        // getProjectConfigurations().get().each{
        //     println("--${it}")
        // }
    }

}