package dk.fust.provenance.generator.erdiagram.generators

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.MarkdownDestination
import dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration
import dk.fust.provenance.generator.erdiagram.ERDiagramGenerator
import dk.fust.provenance.generator.erdiagram.GenerateKey
import dk.fust.provenance.model.Provenance
import spock.lang.Specification

class MermaidGeneratorSpec extends Specification {

    def "generate mermaid ER diagram"() {
        given:
        MermaidGenerator mermaidGenerator = new MermaidGenerator()
        GeneratorConfiguration conf = new ERDiagramConfiguration()
        Provenance provenance = TestHelper.loadTestProvenance('provenance-erdiagram.yaml')

        when:
        String uml = mermaidGenerator.generateUML(null, provenance, null, conf)

        then:
        uml.contains 'table_a'
        uml.contains 'table_b'
        uml.contains 'table_b ||--o{ table_a : ""'

        and: 'combined foreign keys are generated as expected'
        uml.contains '''combined_foreign_key_table {
    INT field_b_combined FK
}'''
        uml.contains 'table_b ||--o{ combined_foreign_key_table : ""\n'
    }

    def "generate mermaid ER diagram with external tables"() {
        given:
        File markdownFile = generateMarkdown('target/test-external-mermaid.md')
        GeneratorConfiguration conf = new ERDiagramConfiguration(
                umlGenerator: UMLGenerator.MERMAID,
                generateKeys: [
                        new GenerateKey(destinationKey: 'DSA_ERD')
                ],
                destination: new MarkdownDestination(
                        file: new File('target/test-external-mermaid.md')
                ),
                externalProvenanceFiles: [TestHelper.getTestFile('provenance-erdiagram-external.yaml')]
        )
        ERDiagramGenerator erDiagramGenerator = new ERDiagramGenerator()

        Provenance provenance = TestHelper.loadTestProvenance('provenance-erdiagram-needs-external.yaml')

        when:
        erDiagramGenerator.generate(provenance, conf)
        String writtenContents = markdownFile.text

        then:
        noExceptionThrown()
        writtenContents == """
[//]: #DSA_ERD_START ()
```mermaid
---
title: The provenance title
---
erDiagram
    classDef externalRef stroke-dasharray: 1 1;
    external_table ||--o{ needs_table : ""
    class external_table externalRef
needs_table {
    TEXT name FK
}
    external_table ||--o{ needs_table_with_generated_id : ""
    class external_table externalRef
    external_table_with_generated_id ||--o{ needs_table_with_generated_id : ""
    class external_table_with_generated_id externalRef
needs_table_with_generated_id {
    TEXT name FK
    INT some_id FK
}

```
[//]: #DSA_ERD_END ()
"""
    }

    protected static File generateMarkdown(String filename) {
        File markdownFile = new File(filename)
        if (markdownFile.exists()) {
            markdownFile.delete()
        }
        markdownFile << """
[//]: #DSA_ERD_START ()
SOMETHING TO BE REPLACED
[//]: #DSA_ERD_END ()
"""
        return markdownFile
    }
}
