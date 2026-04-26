package dk.fust.provenance.generator.erdiagram.generators

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration
import dk.fust.provenance.model.Provenance
import spock.lang.Specification

class MermaidGeneratorSpec extends Specification {

    def "generate mermaid ER diagram"() {
        given:
        MermaidGenerator mermaidGenerator = new MermaidGenerator()
        GeneratorConfiguration conf = new ERDiagramConfiguration()
        Provenance provenance = TestHelper.loadTestProvenance('provenance-erdiagram.yaml')

        when:
        String uml = mermaidGenerator.generateUML(null, provenance, conf)

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

}
