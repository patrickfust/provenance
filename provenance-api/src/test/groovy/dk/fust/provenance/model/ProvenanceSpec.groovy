package dk.fust.provenance.model

import spock.lang.Specification
import spock.lang.Unroll

class ProvenanceSpec extends Specification {

    @Unroll
    def "setting generate id on table to #generateId"() {
        given:
        Provenance provenance = new Provenance()
        provenance.tables = [new Table(name: 'table', generation: [generateId: generateId])]

        when:
        Table table = provenance.getTable('table')
        Generation generationForTable = provenance.getGenerationForTable(table)

        then:
        generationForTable.hasGenerateId()
        generationForTable.getGenerateId() == generateId

        where:
        generateId << [true, false]
    }

}
