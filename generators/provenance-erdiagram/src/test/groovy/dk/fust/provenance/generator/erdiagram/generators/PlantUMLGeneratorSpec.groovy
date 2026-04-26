package dk.fust.provenance.generator.erdiagram.generators

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration
import dk.fust.provenance.model.Provenance
import spock.lang.Specification

class PlantUMLGeneratorSpec extends Specification {

    def "generate plantUML ER diagram"() {
        given:
        PlantUMLGenerator plantUMLGenerator = new PlantUMLGenerator()
        GeneratorConfiguration conf = new ERDiagramConfiguration()
        Provenance provenance = TestHelper.loadTestProvenance('provenance-erdiagram.yaml')

        when:
        String uml = plantUMLGenerator.generateUML(null, provenance, conf)

        then:
        uml.contains 'table_a'
        uml.contains 'table_b'
        uml.contains 'xxx.table_b::field_b ||--o{ xxx.table_a::field_a'

        and: 'combined foreign keys are generated as expected'
        uml.contains '$fk("field_b_combined"): int'
        uml.contains 'xxx.table_b::field_b ||--o{ xxx.combined_foreign_key_table::field_b_combined'
    }

}
