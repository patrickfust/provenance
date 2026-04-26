package dk.fust.provenance.format.table

import dk.fust.provenance.TestData
import spock.lang.Specification

class HTMLTableFormatterSpec extends Specification {

    def "generate table"() {
        given:
        FormatTable table = TestData.generateTable()

        when:
        HTMLTableFormatter htmlTableFormat = new HTMLTableFormatter()
        String htmlTable = htmlTableFormat.formatTable(table)

        then:
        htmlTable.contains '<table'
        htmlTable.contains(table.tableClass)
        println htmlTable
    }

}
