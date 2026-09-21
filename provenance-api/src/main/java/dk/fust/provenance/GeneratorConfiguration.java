package dk.fust.provenance;

import dk.fust.provenance.destination.Destination;

import java.io.File;
import java.util.List;

/**
 * This interface is the one that the generators will use to get the desired configuration.
 */
public interface GeneratorConfiguration {

    /**
     * Validates that the configuration are in order
     */
    void validate();

    /**
     * Location of the provenance file
     * @return Location of the provenance file
     */
    File getProvenanceFile();

    /**
     * List of external provenance files to include in the generation. These files will be loaded and can be referenced in the generation process.
     * @return List of external provenance files to include in the generation
     */
    default List<String> getExternalProvenanceFiles() {
        return null;
    }

    /**
     * Returns the specific implementation of a generator that can fulfill this configuration
     * @return the instance of the generator
     */
    Generator getGenerator();

    /**
     * Where to send the artifact
     * @return Where to send the artifact
     */
    Destination getDestination();

}
