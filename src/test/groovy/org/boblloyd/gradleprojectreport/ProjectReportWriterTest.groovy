package org.boblloyd.gradleprojectreport

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class ProjectReportWriterTest{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    File outputFolder
    File reportFile
    ProjectReportWriter reportWriter
    ProjectReportModel reportModel

    @Before
    public void setup(){
        outputFolder = tempFolder.newFolder()
        reportFile = new File(outputFolder, "testReport.md")
        reportWriter = new ProjectReportWriter()
        reportModel = new ProjectReportModel()
        reportModel.description = "My Project Description"
        reportModel.group = "org.test.group"
        reportModel.name = "testWriteFile"
        reportModel.configurations = [:]
    }

    @Test
    public void testWriteFile_FileCreated(){
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not exist as expected", reportFile.exists())
    }

    @Test
    public void testWriteFile_FileContainsProjectMetaDataHeader(){
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain project metadata header", reportFile.text.contains("# Project Metadata"))
    }

    @Test
    public void testWriteFile_FileContainsProjectName(){
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain name", reportFile.text.contains("## Name: testWriteFile"))
    }

    @Test
    public void testWriteFile_FileContainsDifferentProjectName(){
        reportModel.name = "this_is_another_project_name"
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain name", reportFile.text.contains("## Name: this_is_another_project_name"))
    }

    @Test
    public void testWriteFile_FileContainsDescription(){
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain description", reportFile.text.contains("""## Description:
```
My Project Description
```"""))
    }

    @Test
    public void testWriteFile_NoDescription(){
        reportModel.description = ""
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain description", reportFile.text.contains("""## Description:"""))
        assertFalse("File contains extra description when no description was provided", reportFile.text.contains("""```"""))
    }

    @Test
    public void testWriteFile_FileContainsDifferentDescription(){
        reportModel.description = "Completely Different Description Of The Project"
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain description", reportFile.text.contains("""## Description:
```
Completely Different Description Of The Project
```"""))
    }

    @Test
    public void testWriteFile_FileContainsMultiLineDescription(){
        reportModel.description = """This Description
                                     spans multiple lines"""
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain description", reportFile.text.contains("""## Description:
```
This Description
                                     spans multiple lines
```"""))
    }

    @Test
    public void testWriteFile_FileContainsGroup(){
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain version", reportFile.text.contains("## Group: org.test.group"))
    }

    @Test
    public void testWriteFile_FileContainsDifferentGroup(){
        reportModel.group = "com.company"
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain version", reportFile.text.contains("## Group: com.company"))
    }

    @Test
    public void testWriteFile_NoConfigurationGiven(){
        reportWriter.writeReport(reportModel, reportFile)
        assertFalse("File contains the configuration header, when it shouldn't", reportFile.text.contains("# Dependencies"))
    }

    @Test
    public void testWriteFile_OneConfigurationGiven_CreatesDependenciesHeader(){
        reportModel.configurations = ['configurationA': ['junit:junit:4.12 - junit-4.12.jar']]
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain the Dependencies header", reportFile.text.contains("# Dependencies"))
    }

    @Test
    public void testWriteFile_OneConfigurationGiven_CreatesConfigurationHeaders(){
        reportModel.configurations = ['configurationA': ['junit:junit:4.12 - junit-4.12.jar']]
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain the Dependencies header", reportFile.text.contains("## configurationA"))
    }

    @Test
    public void testWriteFile_TwoConfigurationsGiven_CreatesConfigurationHeaders(){
        reportModel.configurations = [
            'configurationA': ['junit:junit:4.12 - junit-4.12.jar'],
            'configurationB': ['com.company:example:1.2.3 - example-1.2.3.jar']
        ]
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain the Dependencies header", reportFile.text.contains("## configurationA"))
        assertTrue("File does not contain the Dependencies header", reportFile.text.contains("## configurationB"))
    }

    @Test
    public void testWriteFile_OneConfigurationGiven_WriteDependencies(){
        reportModel.configurations = ['configurationA': ['junit:junit:4.12 - junit-4.12.jar']]
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain the dependencies", reportFile.text.contains("* junit:junit:4.12 - junit-4.12.jar"))
    }

    @Test
    public void testWriteFile_TwoConfigurationsGiven_WriteDependencies(){
        reportModel.configurations = [
            'configurationA': ['junit:junit:4.12 - junit-4.12.jar'],
            'configurationB': ['com.company:example:1.2.3 - example-1.2.3.jar']
        ]
        reportWriter.writeReport(reportModel, reportFile)
        assertTrue("File does not contain the dependencies", reportFile.text.contains("* junit:junit:4.12 - junit-4.12.jar"))
        assertTrue("File does not contain the dependencies", reportFile.text.contains("* com.company:example:1.2.3 - example-1.2.3.jar"))
    }

    @Test
    public void testWriteFile_TwoConfigurationsGiven_WriteDependencies_InTheRightOrder(){
        reportModel.configurations = [
            'configurationA': ['junit:junit:4.12 - junit-4.12.jar'],
            'configurationB': ['com.company:example:1.2.3 - example-1.2.3.jar']
        ]
        reportWriter.writeReport(reportModel, reportFile)
        def lines = reportFile.readLines()
        for(int i=0; i < lines.size(); i++){
            if(lines[i] == "# Dependencies"){
                assertEquals(lines[i+1], "## configurationA")
                assertEquals(lines[i+2], "* junit:junit:4.12 - junit-4.12.jar")
                assertEquals(lines[i+3], "## configurationB")
                assertEquals(lines[i+4], "* com.company:example:1.2.3 - example-1.2.3.jar")
            }
        }
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void testWriteFile_FileIsReadOnly(){
        outputFolder.setReadOnly()
        try{
            reportWriter.writeReport(reportModel, reportFile)
        } finally {
            assertFalse("File exists when it shouldn't", reportFile.exists())
        }
    }
}