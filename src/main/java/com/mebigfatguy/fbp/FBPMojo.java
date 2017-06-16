package com.mebigfatguy.fbp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

@Mojo(name = "fbp", requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class FBPMojo extends AbstractMojo {

    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    private Settings settings;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "reactorProjects", readonly = true, required = true)
    private List<MavenProject> reactorProjects;

    @Parameter(property = "outputFile")
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException {

        try (PrintWriter pw = getFBPStream()) {

            pw.println("<Project projectName=\"" + project.getName() + "\">");
            pw.println("\t<Jar>" + project.getBuild().getOutputDirectory() + "</Jar>");
            for (MavenProject module : reactorProjects) {
                pw.println("\t<Jar>" + module.getBuild().getOutputDirectory() + "</Jar>");
            }

            Set<Dependency> dependencies = new HashSet<>();
            dependencies.addAll(project.getCompileDependencies());

            for (MavenProject module : reactorProjects) {
                dependencies.addAll(module.getCompileDependencies());
            }

            String localRepo = settings.getLocalRepository();
            if (!localRepo.endsWith("/") && !localRepo.endsWith("\\")) {
                localRepo += "/";
            }

            for (Dependency dependency : dependencies) {
                pw.println("\t<AuxClasspathEntry>" + localRepo + dependency.getGroupId().replace('.', '/') + "/" + dependency.getArtifactId() + "/"
                        + dependency.getVersion() + "/" + dependency.getArtifactId() + "-" + dependency.getVersion() + "." + dependency.getType()
                        + "</AuxClasspathEntry>");
            }

            Set<String> srcRoots = new HashSet<>();
            srcRoots.addAll(project.getCompileSourceRoots());
            for (MavenProject module : reactorProjects) {
                srcRoots.addAll(module.getCompileSourceRoots());
            }

            for (String srcRoot : srcRoots) {
                pw.println("\t<SrcDir>" + srcRoot + "</SrcDir>");
            }

            pw.println("</Project>");

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
