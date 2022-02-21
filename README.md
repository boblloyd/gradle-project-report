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

# Conventions and Considerations

## Plugin Location

The plugin is not available publicly at this time.  As this requires uploading the plugin to a central repository, and this is a coding demonstration, this functionality was not implemented.  Therefore, it is required that the plugin be added explicitly, after being built from source, before it can be used.

## Testing

Test coverage is not 100% at this time.  While sufficient edge case testing has been completed, there are not unit tests for all use cases.  Much of this functionality was done in the Integration Test phase, as Gradle plugin application and task generation requires significant abstraction and mocking.

Unit tests exist for lower level functionality, such as writing the report to the markdown file.  While this can easily be abstracted using jUnit temporary files, testing the task, extension, and plugin classes would be much more difficult.  Cost versus benefit was not significant to move this testing to the integration test phase, via Gradle TestKit functionality.

### Gradle TestKit

This project only tests the Gradle 7.x major versions.  There is functionality built into the Gradle build to collect the versions of Gradle that are available, to keep this list up to date.  Using this list of versions, integraitonTest tasks are created, one for each version of Gradle.  This was the simplest thing that works, and could be improved.

The functionality queries the Gradle distribution site (https://services.gradle.org/distributions/) and finds the versions of Gradle 7.x, disregarding RC and Milestone versions.  For each version found, a new `integrationTest` task is created, with the relevant version number.  This version number is configured as a system property for the test, for the Gradle TestKit to use to run that version of Gradle.

This could be improved in the future in a number of ways

* Rewriting the integration tests to use a framework such as Spock could be useful.  It's been many years since I've used Spock, and I felt the time was better spent elsewhere than relearning the framework.  It would definitely help to parameterize the builds, as jUnit parameterization is not as easy.
* jUnit does have a parameterized functionality.  However, this functionality is not trivial, as jUnit uses multiple classes and runners to perform this functionality.
* Getting the list of versions from the remote site, and parsing HTML responses is not idea.  I'm unaware at this time if there is an API to get this data, but this was as simple as I could make it right now.
* * This also adds the effect of performing the query to the Gradle distributions site each time the build is run.  This would be better suited to be cached in the future, and only queried as a changing dependency (i.e. after some time) as Gradle releases are not made that often.
* * This could also be implemented into a plugin of it's own to retrieve this version list.  Providing the list of acceptable major versions, and combining them with additional test iterations would be more effective.
