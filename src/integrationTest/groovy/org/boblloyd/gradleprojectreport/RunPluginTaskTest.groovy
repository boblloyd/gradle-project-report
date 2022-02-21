package org.boblloyd.gradleprojectreport

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.FAILED

/*
 * These tests confirm that the configuration of the plugin is functional
 *  and that the properties we expect are the same.  If any of the properties
 *  or configuration variable names change, these tests should fail. 
 */

class RunPluginTaskTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public File projectFolder
    public File buildFile
    public File gradlePropertiesFile
    public File settingsFile
    public File reportFile

    @Before
    public void setup() {
        String projectName = 'test-project'
        projectFolder = tempFolder.newFolder()
        buildFile = new File(projectFolder, "build.gradle");
        settingsFile = new File(projectFolder, 'settings.gradle')
        gradlePropertiesFile = new File(projectFolder, "gradle.properties");

        String buildFileContent = "plugins {\n" + 
                                  "  id 'org.boblloyd.GradleProjectReport'\n" +
                                  "}";
        writeFile(buildFile, buildFileContent);
        String settingsContent = "rootProject.name = '${projectName}'"
        writeFile(settingsFile, settingsContent)
        reportFile = new File(projectFolder, "build/reports/${projectName}.md")
    }

    @Test
    public void runPluginTask_HappyPath(){
        String gradlePropertiesFileContent = "description=This is my sample project\n" +
                                             "group=org.test.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def result = build('projectReport')

        assertTrue("Report File Was Not Written", reportFile.exists())
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
    }

    @Test
    public void runPluginTask_ContainsData(){
        String gradlePropertiesFileContent = "description=This is my sample project\n" +
                                             "group=org.test.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def result = build('projectReport')

        assertTrue("Report file doesn't contain description", reportFile.text.contains("This is my sample project"))
        assertTrue("Report file doesn't contain group", reportFile.text.contains("org.test.project"))
        assertTrue("Report file doesn't contain name", reportFile.text.contains("test-project"))
        assertFalse("Report file contains dependencies section, without renderDependencies set to true", reportFile.text.contains("# Dependencies"))
    }

    @Test
    public void runPluginTask_HappyPath_ProjectHasDependencies(){
        String gradlePropertiesFileContent = "description=This is my sample project\n" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def additionalBuildContent = "\nprojectReport {\n" + 
                                 "  renderDependencies=true\n" +
                                 "}\n" + 
                                 "repositories{mavenCentral()}\n" +
                                 "configurations{\n" +
                                 "  compileRuntime\n" +
                                 "}\n" +
                                 "dependencies{\n" +
                                 "  compileRuntime 'junit:junit:4.12'\n" +
                                 "}\n"
        writeFile(buildFile, additionalBuildContent, true)

        def result = build('projectReport')
        assertTrue("Report file does not contain Dependencies section", reportFile.text.contains("# Dependencies"))
        assertTrue("Report file does not contain compileRuntime section", reportFile.text.contains("## compileRuntime"))
        assertTrue("Report file does not contain junit dependency", reportFile.text.contains("* junit:junit:4.12 - junit-4.12.jar"))
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
    }

    @Test
    public void runPluginTask_HappyPath_ProjectHasFileDependencies(){
        String gradlePropertiesFileContent = "description=This is my sample project\n" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def additionalBuildContent = "\nprojectReport {\n" + 
                                 "  renderDependencies=true\n" +
                                 "}\n" + 
                                 "repositories{mavenCentral()}\n" +
                                 "configurations{\n" +
                                 "  compileRuntime\n" +
                                 "}\n" +
                                 "dependencies{\n" +
                                 "  compileRuntime files('/Path/to/my/file.jar')\n" +
                                 "}\n"
        writeFile(buildFile, additionalBuildContent, true)

        def result = build('projectReport')
        assertTrue("Report file does not contain Dependencies section", reportFile.text.contains("# Dependencies"))
        assertTrue("Report file does not contain compileRuntime section", reportFile.text.contains("## compileRuntime"))
        assertTrue("Report file does not contain junit dependency", reportFile.text.contains("* file:: - /Path/to/my/file.jar"))
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
    }

    @Test
    public void runPluginTask_HappyPath_ProjectHasDependencies_RenderDependenciesNotSet(){
        String gradlePropertiesFileContent = "description=This is my sample project\n" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def additionalBuildContent =  "\nrepositories{mavenCentral()}\n" +
                                 "configurations{\n" +
                                 "  compileRuntime\n" +
                                 "}\n" +
                                 "dependencies{\n" +
                                 "  compileRuntime 'junit:junit:4.12'\n" +
                                 "}\n"
        writeFile(buildFile, additionalBuildContent, true)
        def result = build('projectReport')
        assertFalse("Report file contains Dependencies section", reportFile.text.contains("# Dependencies"))
        assertFalse("Report file contains compileRuntime section", reportFile.text.contains("## compileRuntime"))
        assertFalse("Report file contains junit dependency", reportFile.text.contains("* junit:junit:4.12 - junit-4.12.jar"))
    }

    private def build(String args){
        return GradleRunner.create()
            .withProjectDir(projectFolder)
            .withArguments(args)
            .withPluginClasspath()
            .build()
    }

    private void writeFile(File destination, String content, boolean append = false) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination, append));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}