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

class ApplyPluginTest {
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

        String buildFileContent = "plugins {" + 
                                  "  id 'org.boblloyd.GradleProjectReport'" +
                                  "}";
        writeFile(buildFile, buildFileContent);

        String gradlePropertiesFileContent = "version=1.0.1" +
                                             "group=org.test.project"
        writeFile(gradlePropertiesFile, gradlePropertiesFileContent)
    }

    @Test
    public void applyPlugin_HappyPath(){
        def result = GradleRunner.create()
            .withProjectDir(projectFolder)
            .withArguments('projectReport')
            .withPluginClasspath()
            .build()

        assertTrue("Wrong version was reported by the plugin.", result.getOutput().contains("1.0.1"));
        assertTrue("Wrong group ID was reported by the plugin.", result.getOutput().contains("org.test.project"));
        assertEquals(SUCCESS, result.task(":projectReport").getOutcome());
    }

    private void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}