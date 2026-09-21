package dk.fust.provenance.generator.erdiagram.generators

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.MarkdownDestination
import dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration
import dk.fust.provenance.generator.erdiagram.ERDiagramGenerator
import dk.fust.provenance.generator.erdiagram.GenerateKey
import dk.fust.provenance.model.Provenance
import spock.lang.Specification

class PlantUMLGeneratorSpec extends Specification {

    def "generate plantUML ER diagram"() {
        given:
        PlantUMLGenerator plantUMLGenerator = new PlantUMLGenerator()
        GeneratorConfiguration conf = new ERDiagramConfiguration()
        Provenance provenance = TestHelper.loadTestProvenance('provenance-erdiagram.yaml')

        when:
        String uml = plantUMLGenerator.generateUML(null, provenance, null, conf)

        then:
        uml.contains 'table_a'
        uml.contains 'table_b'
        uml.contains 'xxx.table_b::field_b ||--o{ xxx.table_a::field_a'

        and: 'combined foreign keys are generated as expected'
        uml.contains '$fk("field_b_combined"): int'
        uml.contains 'xxx.table_b::field_b ||--o{ xxx.combined_foreign_key_table::field_b_combined'
    }

    def "generate plantUML ER diagram with external tables"() {
        given:
        File markdownFile = MermaidGeneratorSpec.generateMarkdown('target/test-external-plantuml.md')
        GeneratorConfiguration conf = new ERDiagramConfiguration(
                umlGenerator: UMLGenerator.PLANTUML,
                generateKeys: [
                        new GenerateKey(destinationKey: 'DSA_ERD')
                ],
                destination: new MarkdownDestination(
                        file: new File('target/test-external-plantuml.md')
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
        writtenContents == '''
[//]: #DSA_ERD_START ()
```plantuml
@startuml

!theme plain
hide empty methods

!procedure $schema($name)
    package "$name" <<Rectangle>>
!endprocedure
!procedure $table($name)
    entity "<b>$name</b>" as $name << (T, Orange) table >>
!endprocedure
!procedure $view($name)
    entity "<b>$name</b>" as $name << (V, Aquamarine) view >>
!endprocedure
!procedure $pk($name)
    <color:#GoldenRod><&key></color> <b>$name</b>
!endprocedure
!procedure $fk($name)
    <color:#Silver><&key></color> $name
!endprocedure
!procedure $column($name)
   {field} <color:#White><&media-record></color> $name
!endprocedure
title "The provenance title"
$schema("abc") {
  $table("needs_table") {
    $fk("name"): text
  }
  $table("needs_table_with_generated_id") {
    $fk("name"): text
    $fk("some_id"): int
  }
}
abc.external_table::name ||--o{ abc.needs_table::name
abc.external_table::name ||--o{ abc.needs_table_with_generated_id::name
abc.external_table_with_generated_id::external_table_with_generated_id_id ||--o{ abc.needs_table_with_generated_id::some_id
@enduml
```
[//]: #DSA_ERD_END ()
'''
    }

}
