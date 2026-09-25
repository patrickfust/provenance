package dk.fust.provenance.generator.erdiagram.generators;

import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.model.Provenance;

import java.util.List;

/**
 * Interface for different types of diagrams
 */
public interface ERGenerator {

    /**
     * Id for the type of diagram renderer. Ie plantuml and mermaid
     * @return diagram in mermaid format
     */
    String getMarkdownType();

    /**
     * Go generate er-diagram please
     * @param filterTags filter for which groups to render. It finds the tables that have a matching tag. If null or empty, all groups are rendered.
     * @param provenance model containing the tables to render
     * @param externalProvenanceFiles list of external provenance files to lookup into when rendering
     * @param generatorConfiguration configuration for how to render
     * @return Markdown markup
     */
    String generateUML(String filterTags, Provenance provenance, List<Provenance> externalProvenanceFiles, GeneratorConfiguration generatorConfiguration);

}
