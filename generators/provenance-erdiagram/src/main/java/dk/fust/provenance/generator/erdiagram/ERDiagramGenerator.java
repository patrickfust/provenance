package dk.fust.provenance.generator.erdiagram;

import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.Generator;
import dk.fust.provenance.generator.erdiagram.generators.ERGenerator;
import dk.fust.provenance.generator.erdiagram.generators.ERGeneratorFactory;
import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.util.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Generator for entity-relation diagrams
 */
@Slf4j
public class ERDiagramGenerator implements Generator {
    @Override
    public void generate(Provenance provenance, GeneratorConfiguration generatorConfiguration) throws IOException {
        Assert.isTrue(generatorConfiguration instanceof ERDiagramConfiguration, "generatorConfiguration is not a ERDiagramConfiguration");
        ERDiagramConfiguration erDiagramConfiguration = (ERDiagramConfiguration) generatorConfiguration;
        log.debug("Generating ERDiagram...");
        Assert.isNotNull(erDiagramConfiguration.getDestination(), "destination must not be null");
        erDiagramConfiguration.getDestination().validate();
        ERGenerator generator = ERGeneratorFactory.getGenerator(erDiagramConfiguration.getUmlGenerator());
        for (GenerateKey generateKey : erDiagramConfiguration.getGenerateKeys()) {
            String uml = generator.generateUML(generateKey.getFilterTags(), provenance, generatorConfiguration);
            String document = """
```%s
%s
```
""".formatted(generator.getMarkdownType(), uml);
            erDiagramConfiguration.getDestination().sendDocumentToDestination(document, generateKey.getDestinationKey());
        }

    }
}
