package org.boblloyd.gradleprojectreport

class ProjectReportWriter{
    public void writeReport(ProjectReportModel model, File reportFile){
        String contents = ""
        contents += "# Project Metadata\n"
        contents += "## Group: ${model.group}\n"
        contents += "## Name: ${model.name}\n"
        contents += "## Description:\n"
        if(model.description && model.description.length() > 0){
            contents += "```\n"
            contents += "${model.description}\n"
            contents += "```\n\n"
        }

        if(model.configurations && model.configurations.size() > 0){
            contents += "# Dependencies\n"
            model.configurations.each{name, dependencies ->
                contents += "## ${name}\n"
                dependencies.each{ dependency ->
                    contents += "* ${dependency}\n"
                }
            }
        }
        
        try{
            writeFile(reportFile, contents)
        } catch (java.io.FileNotFoundException e){
            println("Could not write to the file at location ${reportFile.toString()}.\n\tPlease confirm you have write permissions to the folder, and the file is not locked, or the disk is full.\n\tReport File: ${reportFile.toString()}")
            throw e
        }
    }

    private void writeFile(File destination, String content, boolean append = false) {
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