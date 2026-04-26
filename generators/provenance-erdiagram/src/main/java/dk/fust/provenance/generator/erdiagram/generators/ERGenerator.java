package dk.fust.provenance.generator.erdiagram.generators;

import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.model.Provenance;

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
     * @param generatorConfiguration configuration for how to render
     * @return markdown markup
     */
    String generateUML(String filterTags, Provenance provenance, GeneratorConfiguration generatorConfiguration);

}
