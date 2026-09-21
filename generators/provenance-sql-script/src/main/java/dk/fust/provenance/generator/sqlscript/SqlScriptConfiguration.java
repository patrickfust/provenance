package dk.fust.provenance.generator.sqlscript;

import dk.fust.provenance.AbstractGeneratorConfiguration;
import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.generator.sqlscript.generators.SqlDialect;
import dk.fust.provenance.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.File;

/**
 * Configuration for generating SQL scripts
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SqlScriptConfiguration extends AbstractGeneratorConfiguration {

    private String filterTags;

    /**
     * Which dialect must the sql be? Default: POSTGRES
     */
    private SqlDialect sqlDialect = SqlDialect.POSTGRES;

    @Override
    public void validate() {
        Assert.isNotNull(getProvenanceFile(), "provenanceFile must not be null");
        Assert.isNotNull(getDestination(), "destination must not be null");
        getDestination().validate();
    }

    @Override
    public Generator getGenerator() {
        return new SqlScriptGenerator();
    }

}
