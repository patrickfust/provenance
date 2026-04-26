package dk.fust.provenance;

import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.service.ProvenanceService;
import dk.fust.provenance.util.Assert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main class - Everything starts here
 * This class loops over all the configurations and run their generators
 */
@Slf4j
public class ProvenanceGenerator {

    private final ProvenanceService provenanceService = new ProvenanceService();

    @Setter
    private File baseDir;

    /**
     * Loops over all the configurations and generates the documentation
     * @param generatorConfigurations list of configurations to run
     */
    public void generate(List<GeneratorConfiguration> generatorConfigurations) {
        Assert.isNotNull(generatorConfigurations, "ProvenanceGenerator: generatorConfigurations must not be null");
        generatorConfigurations.forEach(this::generate);
    }

    /**
     * Generates documentation for a single configuration
     * @param generatorConfiguration configuration to generate documentation for
     */
    public void generate(GeneratorConfiguration generatorConfiguration) {
        Assert.isNotNull(generatorConfiguration, "ProvenanceGenerator: Missing configuration");
        Assert.isNotNull(generatorConfiguration.getDestination(), "ProvenanceGenerator: Missing configuration's destination. " + generatorConfiguration);
        generatorConfiguration.validate();
        generatorConfiguration.getDestination().validate();
        try {
            File provenanceFile = generatorConfiguration.getProvenanceFile();
            log.debug("ProvenanceGenerator: Loading provenance file: {}", provenanceFile.getAbsolutePath());
            if (!provenanceFile.exists()) {
                log.debug("Can't find file -> trying in baseDir");
                provenanceFile = new File(baseDir, provenanceFile.getPath());
                log.debug("ProvenanceGenerator: Loading provenance file in baseDir: {}, exists={}", provenanceFile.getAbsolutePath(), provenanceFile.exists());
            }
            Provenance provenance = provenanceService.loadProvenance(provenanceFile);
            validateModel(provenance);
            Generator generator = generatorConfiguration.getGenerator();
            generator.generate(provenance, generatorConfiguration);
        } catch (IOException e) {
            log.error("Could not generate artifacts", e);
            throw new RuntimeException(e);
        }
    }

    private void validateModel(Provenance provenance) {
        log.debug("Validating provenance model...");
        ModelValidator modelValidator = new ModelValidator(provenance);
        modelValidator.validate();
    }

}
