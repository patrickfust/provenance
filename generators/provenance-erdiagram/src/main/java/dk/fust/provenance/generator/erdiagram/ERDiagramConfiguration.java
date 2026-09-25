package dk.fust.provenance.generator.erdiagram;

import dk.fust.provenance.AbstractGeneratorConfiguration;
import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.generator.erdiagram.generators.UMLGenerator;
import dk.fust.provenance.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.File;
import java.util.List;

/**
 * Configuration for generating entity-relation diagrams
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ERDiagramConfiguration extends AbstractGeneratorConfiguration {

    private UMLGenerator umlGenerator = UMLGenerator.MERMAID;

    private List<GenerateKey> generateKeys;

    @Override
    public void validate() {
        Assert.isNotNull(getProvenanceFile(), "provenanceFile must not be null");
        Assert.isNotNull(umlGenerator, "umlGenerator must not be null");
        Assert.isNotNull(generateKeys, "generateKeys must not be null");
    }

    @Override
    public Generator getGenerator() {
        return new ERDiagramGenerator();
    }

}
