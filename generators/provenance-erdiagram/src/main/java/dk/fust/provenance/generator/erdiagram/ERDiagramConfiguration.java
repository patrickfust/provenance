package dk.fust.provenance.generator.erdiagram;

import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.generator.erdiagram.generators.UMLGenerator;
import dk.fust.provenance.util.Assert;
import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * Configuration for generating entity-relation diagrams
 */
@Data
public class ERDiagramConfiguration implements GeneratorConfiguration {

    private UMLGenerator umlGenerator = UMLGenerator.MERMAID;

    private List<GenerateKey> generateKeys;

    private File provenanceFile;

    private Destination destination;

    @Override
    public void validate() {
        Assert.isNotNull(provenanceFile, "provenanceFile must not be null");
        Assert.isNotNull(umlGenerator, "umlGenerator must not be null");
        Assert.isNotNull(generateKeys, "generateKeys must not be null");
    }

    @Override
    public Generator getGenerator() {
        return new ERDiagramGenerator();
    }

}
