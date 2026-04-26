package dk.fust.provenance.generator.sqlscript;

import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.generator.sqlscript.generators.SqlDialect;
import dk.fust.provenance.util.Assert;
import lombok.Data;

import java.io.File;

/**
 * Configuration for generating SQL scripts
 */
@Data
public class SqlScriptConfiguration implements GeneratorConfiguration {

    private File provenanceFile;

    private Destination destination;

    private String filterTags;

    /**
     * Which dialect must the sql be? Default: POSTGRES
     */
    private SqlDialect sqlDialect = SqlDialect.POSTGRES;

    @Override
    public void validate() {
        Assert.isNotNull(provenanceFile, "provenanceFile must not be null");
        Assert.isNotNull(destination, "destination must not be null");
        destination.validate();
    }

    @Override
    public Generator getGenerator() {
        return new SqlScriptGenerator();
    }

}
