package com.mebigfatguy.fbp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "fbp", requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class FBPMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "outputFile")
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException {

        try (PrintWriter pw = getFBPStream()) {

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

    private PrintWriter getFBPStream() throws IOException {
        if (outputFile == null) {
            return new PrintWriter(new OutputStreamWriter(System.out));
        }

        return new PrintWriter(Files.newBufferedWriter(outputFile.toPath()));
    }
}
