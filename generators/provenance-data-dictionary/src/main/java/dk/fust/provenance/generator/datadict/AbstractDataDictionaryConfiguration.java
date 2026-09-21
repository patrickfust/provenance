package dk.fust.provenance.generator.datadict;

import dk.fust.provenance.AbstractGeneratorConfiguration;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.format.table.MarkdownTableFormatter;
import dk.fust.provenance.format.table.TableFormatter;
import dk.fust.provenance.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

/**
 * Common information for data dictionaries
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractDataDictionaryConfiguration extends AbstractGeneratorConfiguration {

    private String key;

    private String filterTags;

    private TableFormatter tableFormatter = new MarkdownTableFormatter();

    @Override
    public void validate() {
        Assert.isNotNull(getDestination(), "destination is required");
        Assert.isNotNull(getProvenanceFile(), "provenanceFile is required");
        Assert.isNotNull(tableFormatter, "tableFormatter is required");
    }

}
