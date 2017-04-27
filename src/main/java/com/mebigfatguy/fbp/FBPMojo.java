package com.mebigfatguy.fbp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public class FBPMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter
    private File fbpFile;

    @Override
    public void execute() throws MojoExecutionException {

        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(fbpFile.toPath()))) {

            pw.println("<Project projectName=\"" + project.getName() + "\">");

            pw.println("<Jar>" + project.getBuild().getDirectory() + "</Jar>");

            List<Dependency> dependencies = project.getCompileDependencies();
            for (Dependency dependency : dependencies) {
                pw.println(dependency.getGroupId().replace('.', '/') + "/" + dependency.getArtifactId() + "/" + dependency.getVersion() + "/"
                        + dependency.getArtifactId() + "-" + dependency.getVersion() + "." + dependency.getType());
            }

            List<String> srcRoots = project.getCompileSourceRoots();
            for (String srcRoot : srcRoots) {
                pw.println("<SrcDir>" + srcRoot + "</SrcDir>");
            }

            pw.println("<Project>");

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate fbp file", e);
        }
    }
}
