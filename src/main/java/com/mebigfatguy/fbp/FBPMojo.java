package com.mebigfatguy.fbp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.execution.MavenSession;
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

    private static boolean executed = false;

    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    private Settings settings;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "session", readonly = true, required = true)
    private MavenSession session;

    @Parameter(property = "outputFile")
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException {

        if (executed) {
            return;
        }
        executed = true;

        try (PrintWriter pw = getFBPStream()) {

            pw.println("<Project projectName=\"" + project.getName() + "\">");

            List<MavenProject> projects = session.getProjectDependencyGraph().getSortedProjects();

            Set<String> jars = new TreeSet<>();
            for (MavenProject module : projects) {
                jars.add(module.getBuild().getOutputDirectory());
            }

            for (String jar : jars) {
                pw.println("\t<Jar>" + makeRelativePath(jar) + "</Jar>");
            }

            Set<Dependency> dependencies = new TreeSet<>();
            for (MavenProject module : projects) {
                dependencies.addAll(module.getDependencies());
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

            Set<String> srcRoots = new TreeSet<>();
            for (MavenProject module : projects) {
                srcRoots.addAll(module.getCompileSourceRoots());
            }

            for (String srcRoot : srcRoots) {
                pw.println("\t<SrcDir>" + makeRelativePath(srcRoot) + "</SrcDir>");
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

    private String makeRelativePath(String path) {
        if (outputFile == null) {
            return path;
        }

        String outputPath = outputFile.getParent();

        if (path.startsWith(outputPath)) {
            return "." + path.substring(outputPath.length());
        }

        return path;
    }

}
