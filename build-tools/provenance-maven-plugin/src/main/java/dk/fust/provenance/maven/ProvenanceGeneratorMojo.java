package dk.fust.provenance.maven;

import dk.fust.provenance.ProvenanceGenerator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.service.ProvenanceConfigurationLoaderService;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maven plugin for provenance generator
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.NONE)
public class ProvenanceGeneratorMojo extends AbstractMojo {

    @Parameter(property = "provenanceConfigurationFile")
    private File provenanceConfigurationFile;

    @Parameter(property = "provenanceConfigurationFiles")
    private List<File> provenanceConfigurationFiles;

    @Override
    public void execute() throws MojoExecutionException {
        boolean hasSingle = provenanceConfigurationFile != null;
        boolean hasMultiple = provenanceConfigurationFiles != null && !provenanceConfigurationFiles.isEmpty();

        if (!hasSingle && !hasMultiple) {
            throw new MojoExecutionException("Either provenanceConfigurationFile or provenanceConfigurationFiles must be set");
        }
        if (hasSingle && hasMultiple) {
            throw new MojoExecutionException("Cannot set both provenanceConfigurationFile and provenanceConfigurationFiles");
        }

        List<File> filesToProcess = new ArrayList<>();
        if (hasSingle) {
            filesToProcess.add(provenanceConfigurationFile);
        } else {
            filesToProcess.addAll(provenanceConfigurationFiles);
        }

        getLog().info("Parsing provenanceConfigurationFile(s)");
        try {
            ProvenanceConfigurationLoaderService loaderService = new ProvenanceConfigurationLoaderService();
            ProvenanceGenerator provenanceGenerator = new ProvenanceGenerator();
            for (File file : filesToProcess) {
                getLog().info("Processing: " + file.getPath());
                List<GeneratorConfiguration> confs = loaderService.readConfigurations(file);
                provenanceGenerator.generate(confs);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't load generator configurations", e);
        }
    }

}
