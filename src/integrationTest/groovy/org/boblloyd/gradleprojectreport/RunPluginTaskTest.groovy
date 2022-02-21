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

    @Before
    public void setup() {
        projectFolder = tempFolder.newFolder()
        buildFile = new File(projectFolder, "build.gradle");
        gradlePropertiesFile = new File(projectFolder, "gradle.properties");

        String buildFileContent = "plugins {\n" + 
                                  "  id 'org.boblloyd.GradleProjectReport'\n" +
                                  "}";
        writeFile(buildFile, buildFileContent);
    }

    @Test
    public void runPluginTask_HappyPath(){
        String gradlePropertiesFileContent = "version=1.0.1" +
                                             "group=org.test.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def result = build('projectReport')

        assertTrue("Wrong version was reported by the plugin.", result.getOutput().contains("1.0.1"));
        assertTrue("Wrong group ID was reported by the plugin.", result.getOutput().contains("org.test.project"));
        // assertTrue("Wrong renderDependencies value was reported by the plugin.", result.getOutput().contains("Is Show Dependencies: false"));
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
    }

    @Test
    public void runPluginTask_HappyPath_DifferentValues(){
        String gradlePropertiesFileContent = "version=2.3.4" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def result = build('projectReport')

        assertTrue("Wrong version was reported by the plugin.", result.getOutput().contains("2.3.4"));
        assertTrue("Wrong group ID was reported by the plugin.", result.getOutput().contains("com.company.project"));
        // assertTrue("Wrong renderDependencies value was reported by the plugin.", result.getOutput().contains("Is Show Dependencies: false"));
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
    }

    @Test
    public void runPluginTask_HappyPath_ConfigureViaExtension(){
        String gradlePropertiesFileContent = "version=2.3.4" +
                                             "group=com.company.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
        def additionalBuildContent = "\nprojectReport {\n" + 
                                 "  renderDependencies=true\n" +
                                 "}"
        writeFile(buildFile, additionalBuildContent, true)

        def result = build('projectReport')

        assertTrue("Wrong version was reported by the plugin.", result.getOutput().contains("2.3.4"));
        assertTrue("Wrong group ID was reported by the plugin.", result.getOutput().contains("com.company.project"));
        // assertTrue("Wrong renderDependencies value was reported by the plugin.", result.getOutput().contains("Is Show Dependencies: true"));
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
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