package dk.fust.provenance.gradle;

import dk.fust.provenance.GeneratorConfiguration;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent the configuration possibilities
 */
@Data
public class ProvenanceGeneratorPluginExtension {

    private File provenanceConfigurationFile;
    private List<GeneratorConfiguration> generatorConfigurations = new ArrayList<>();

}
