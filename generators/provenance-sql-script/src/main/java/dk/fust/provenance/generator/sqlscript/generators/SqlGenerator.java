package dk.fust.provenance.generator.sqlscript.generators;

import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.generator.sqlscript.SqlScriptConfiguration;

import java.io.IOException;

/**
 * Generates SQL scripts for a specific database
 */
public interface SqlGenerator {

    /**
     * Generate SQL
     * @param provenance source file with tables, views and indexes
     * @param sqlScriptConfiguration how we want to generation to happen
     * @throws IOException an error occurred
     */
    void generate(Provenance provenance, SqlScriptConfiguration sqlScriptConfiguration) throws IOException;

}
