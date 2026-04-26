package dk.fust.provenance;

import dk.fust.provenance.model.Provenance;

import java.io.IOException;

/**
 * Every generator must implement this.
 * This will then be called from the framework.
 */
public interface Generator {

    /**
     * Generates artifacts for the model
     * @param provenance model to generate for
     * @param generatorConfiguration how and what it must generate
     * @throws IOException if an error occurs
     */
    void generate(Provenance provenance, GeneratorConfiguration generatorConfiguration) throws IOException;

}
