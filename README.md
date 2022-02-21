# Gradle Project Report

The Gradle Project Report is a plugin for Gradle that will report key project metadata to a markdown file.

This data includes the project name, description, and group identifier, as well as optionally reporting on the dependencies for each configuration in the project.

## Configuration

This project is not distributed via the Gradle Plugin network, and needs to be configured directly on the project.  Therefore, the `plugins{ id '' }` format will not function with this plugin, until it is uploaded to the Gradle Plugins network.

To configure the project:

```
buildscript{
    dependencies{
        classpath files("/path/to/libs/gradle-project-report-0.0.1.jar")
    }
}

apply plugin: 'org.boblloyd.GradleProjectReport'
```

## Tasks

This plugin creates a task of type ProjectReportTask called `projectReport`.  This task will handle the generation of the markdown report file, and associated configuration.

To execute the task:

```
./gradlew projectReport
```

## Output

The output will be generated in the `$buildDir/reports/${projectName}.md` folder.  For multi-project builds, there will be one report per project, in each project's relative build output directory.  The location of the report can be overridden with the `output` configuration (see below).

## Task Configuration
### Render Dependencies
To render dependencies, configure the projectReport task to set the `renderDependencies` value to `true`.  By default, this value is set to `false`.

```
projectReport{
    renderDependencies = true
}
```

### Output Directory
You can specify the output directory for the plugin by configuring the `output` property on the `projectReport` task.  By default, this will use the build directory layout.  This value expects a String type pointing to the absolute or relative directory for the report output.  Report names will always be based on the project or subproject name, and cannot be changed.

```
projectReport{
    output = "/Path/to/my/reports/"
}
```

# Building
To build the plugin, we can utilize the `build` or `assemble` commands.  By default, the `build` task depends on `check`, which in turn executes all of the tests.  Runnin gonly `assemble` will compile and produce the necessary JAR file for use in other projects.

```
./gradlew assemble
```

# Caching
This plugin will cache configuration and build artifacts that are used.  For instance, the `renderDependencies` and `output` configuration, as part of the `projectReport` task configuration will be cached, along with the project dependencies.  This will ensure that the task will not be re-run, and dependencies will not be resolved again, if the dependencies, or configuration changes.

This also ensures that if dependencies do change, and only dependencies change, running the `projectReport` task again will regenerate the report with the updated information.

In addition, the output file, the report file, will be cached as well.  If this file is modified or removed, rerunning the task again will cause the task to no longer be up-to-date and will re-run.

# CI
Currently, this project uses GitHub Actions for CI builds.  This is done using the gradle-build-action, using JDK 11 on an Ubuntu (latest) image.  Currently, this CI build pipeline only provides validation (compile and testing) CI builds, and does not implement any continuous delivery mechanisms (deployment or release engineering).  Furthermore, security tests and scan are not currently implemented.

# Conventions and Considerations

## Plugin Location

The plugin is not available publicly at this time.  As this requires uploading the plugin to a central repository, and this is a coding demonstration, this functionality was not implemented.  Therefore, it is required that the plugin be added explicitly, after being built from source, before it can be used.

## Testing

Test coverage is not 100% at this time.  While sufficient functionality, integration, and edge case testing has been completed, there are not unit tests for all branches or lines of code.  Much of this functionality was done in the Integration Test phase, as Gradle plugin application and task generation requires significant abstraction and mocking.

Unit tests exist for lower level functionality, such as writing the report to the markdown file.  While this can easily be abstracted using jUnit temporary files, testing the task, extension, and plugin classes would be much more difficult.  Cost versus benefit was not significant to move this testing to the integration test phase, via Gradle TestKit functionality.

Future consideration should be given to ensuring that the code coverage is monitored for the ProjectReport, ProjectReportExtension, ProjectReportModel, and ProjectReportTask classes.  While these classes are tested via the integrationTests, these tests are slow and costly, when compared to unit tests.  Unit testing over these would, at this time, be more expensive as they require objects from Gradle's API to be mocked or stubbed.

### Gradle TestKit

This project only tests the Gradle 7.x major versions.  There is functionality built into the Gradle build to collect the versions of Gradle that are available, to keep this list up to date.  Using this list of versions, integraitonTest tasks are created, one for each version of Gradle.  This was the simplest thing that works, and could be improved.

The functionality queries the Gradle distribution site (https://services.gradle.org/distributions/) and finds the versions of Gradle 7.x, disregarding RC and Milestone versions.  For each version found, a new `integrationTest` task is created, with the relevant version number.  This version number is configured as a system property for the test, for the Gradle TestKit to use to run that version of Gradle.

This could be improved in the future in a number of ways

* Rewriting the integration tests to use a framework such as Spock could be useful.  It's been many years since I've used Spock, and I felt the time was better spent elsewhere than relearning the framework.  It would definitely help to parameterize the builds, as jUnit parameterization is not as easy.
* jUnit does have a parameterized functionality.  However, this functionality is not trivial, as jUnit uses multiple classes and runners to perform this functionality.
* Getting the list of versions from the remote site, and parsing HTML responses is not idea.  I'm unaware at this time if there is an API to get this data, but this was as simple as I could make it right now.
* * This also adds the effect of performing the query to the Gradle distributions site each time the build is run.  This would be better suited to be cached in the future, and only queried as a changing dependency (i.e. after some time) as Gradle releases are not made that often.
* * This could also be implemented into a plugin of it's own to retrieve this version list.  Providing the list of acceptable major versions, and combining them with additional test iterations would be more effective.
* During generation of the dependencies, when `renderDependencies` is set to `true`, there are times when dependencies may show up as `file:: - /Path/to/file.jar`.  This is the case of dependencies defined by explicit paths to jar files, rather than resolved dependencies through a GAVC.  The use of the `file::` moniker is to assist with identification that this dependency is not resolved from an external resource, but is local to the build system.
* * There are also cases where inter-project dependencies, in the case of a multi-project build, show up in dependency resolution.  These dependencies should show in the normal `group:name:version - file.jar` type format, as normal dependencies are shown.
* Currently, logging for the plugin is done only to standard out, and is minimal.  This could be much improved to provide `info` and `debug` logging, but was not explicitly a requirement.  This would be an improvement for functionality and usability in the long run, however.

### package task
This task exists for the purpose of packaging the main, test, and integration test source code, as well as the build and CI files into a single zip file.  This is for the purpose of easy uploading without manually creating a zip file of the project, and can be re-used if changes needed to be made.