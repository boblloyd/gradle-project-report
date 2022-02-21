package org.boblloyd.gradleprojectreport

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import static org.gradle.testkit.runner.TaskOutcome.*

/*
 * These tests confirm that the configuration of the plugin is functional
 *  and that the properties we expect are the same.  If any of the properties
 *  or configuration variable names change, these tests should fail. 
 */

class ConfigurePluginTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public File projectFolder
    public File buildFile
    public File gradlePropertiesFile
    public String gradleVersion

    @Before
    public void setup() {
        projectFolder = tempFolder.newFolder()
        buildFile = new File(projectFolder, "build.gradle");
        gradlePropertiesFile = new File(projectFolder, "gradle.properties");

        String buildFileContent = "plugins {\n" + 
                                  "  id 'base'\n" + 
                                  "  id 'org.boblloyd.GradleProjectReport'\n" +
                                  "}";
        writeFile(buildFile, buildFileContent);

        if(System.getProperty("gradleVersion") && System.getProperty("gradleVersion") != ""){
            gradleVersion = System.getProperty("gradleVersion")
        }
    }

    @Test
    public void configurePlugin_HappyPath(){
        String gradlePropertiesFileContent = "version=1.0.1" +
                                             "group=org.test.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def result = build('build')

        assertEquals(UP_TO_DATE, result.task(":build").getOutcome());
    }

    @Test
    public void configurePlugin_HappyPath_DifferentValues(){
        String gradlePropertiesFileContent = "version=2.3.4" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def result = build('build')

        assertEquals(UP_TO_DATE, result.task(":build").getOutcome());
    }

    @Test
    public void configurePlugin_HappyPath_ConfigureRenderDependenciesViaExtension(){
        String gradlePropertiesFileContent = "version=2.3.4" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def additionalBuildContent = "\nprojectReport {\n" + 
                                 "  renderDependencies=true\n" +
                                 "}"
        writeFile(buildFile, additionalBuildContent, true)
        def result = build('build')

        assertEquals(UP_TO_DATE, result.task(":build").getOutcome());
    }

    @Test
    public void configurePlugin_HappyPath_ConfigureOutputViaExtension(){
        String gradlePropertiesFileContent = "version=2.3.4" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def additionalBuildContent = "\nprojectReport {\n" + 
                                 "  output='/Path/To/My/Output/Directory'\n" +
                                 "}"
        writeFile(buildFile, additionalBuildContent, true)
        def result = build('build')

        assertEquals(UP_TO_DATE, result.task(":build").getOutcome());
    }

    private def build(String args){
        // If gradleVersion is not specified, then use the default one that is running this build
        def runner = GradleRunner.create()
            .withProjectDir(projectFolder)
            .withArguments(args)
            .withPluginClasspath()
        if(gradleVersion){
            return runner.withGradleVersion(gradleVersion).build()
        } else {
            return runner.build()
        }
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