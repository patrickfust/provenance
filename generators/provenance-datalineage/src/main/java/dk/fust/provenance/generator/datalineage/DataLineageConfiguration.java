package dk.fust.provenance.generator.datalineage;

import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.format.table.MarkdownTableFormatter;
import dk.fust.provenance.format.table.TableFormatter;
import dk.fust.provenance.util.Assert;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Data Lineage
 */
@Data
public class DataLineageConfiguration implements GeneratorConfiguration {

    private File provenanceFile;

    private Destination destination;

    /**
     * Map of other provenance files that you can point to in `source`
     */
    private Map<String, File> sourceProvenanceFiles;

    private String key;

    private TableFormatter tableFormatter = new MarkdownTableFormatter();

    private String filterTags;

    @Override
    public void validate() {
        Assert.isNotNull(provenanceFile, "provenanceFile must not be null");
        Assert.isNotNull(destination, "destination must not be null");
        Assert.isNotNull(key, "key must not be null");
        Assert.isNotNull(tableFormatter, "tableFormatter must not be null");
    }

    @Override
    public Generator getGenerator() {
        return new DataLineageGenerator();
    }

}
