package dk.fust.provenance.generator.datadict;

import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.format.table.MarkdownTableFormatter;
import dk.fust.provenance.format.table.TableFormatter;
import dk.fust.provenance.util.Assert;
import lombok.Data;

import java.io.File;

/**
 * Common information for data dictionaries
 */
@Data
public abstract class AbstractDataDictionaryConfiguration implements GeneratorConfiguration {

    private File provenanceFile;

    private Destination destination;

    private String key;

    private String filterTags;

    private TableFormatter tableFormatter = new MarkdownTableFormatter();

    @Override
    public void validate() {
        Assert.isNotNull(destination, "Destination is required");
        Assert.isNotNull(provenanceFile, "provenanceFile is required");
        Assert.isNotNull(tableFormatter, "TableFormatter is required");
    }

}
