package dk.fust.provenance;

import dk.fust.provenance.destination.Destination;
import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public abstract class AbstractGeneratorConfiguration implements GeneratorConfiguration {

    private File provenanceFile;
    private Destination destination;
    private List<String> externalProvenanceFiles;

}
