package dk.fust.provenance.maven;

import dk.fust.provenance.ProvenanceGenerator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.service.ProvenanceConfigurationLoaderService;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
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
    public void execute() {
        getLog().info("Parsing provenanceConfigurationFile");
        try {
            ProvenanceConfigurationLoaderService provenanceConfigurationLoaderService = new ProvenanceConfigurationLoaderService();
            List<GeneratorConfiguration> confs = provenanceConfigurationLoaderService.readConfigurations(provenanceConfigurationFile);
            ProvenanceGenerator provenanceGenerator = new ProvenanceGenerator();
            provenanceGenerator.generate(confs);
        } catch (IOException e) {
            throw new RuntimeException("Can't load generator configurations", e);
        }
    }

}
