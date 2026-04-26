package dk.fust.provenance.generator.datadict

import dk.fust.provenance.Generator
import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.MockDestination
import dk.fust.provenance.format.table.JsonTableFormatter
import dk.fust.provenance.format.table.TableFormatter
import spock.lang.Specification

class DataDictionaryMetaInfoGeneratorSpec extends Specification {

    def "generate data dictionary meta info as json"() {
        given:
        MockDestination mockDestination = new MockDestination()
        TableFormatter tableFormatter = new JsonTableFormatter()
        DataDictionaryMetaInfoConfiguration configuration = new DataDictionaryMetaInfoConfiguration(
                destination: mockDestination,
                tableFormatter: tableFormatter
        )

        Generator generator = configuration.getGenerator()

        when:
        generator.generate(TestHelper.loadTestProvenance("provenance-data-dictionary.yaml"), configuration);

        then:
        mockDestination.document == """ \
        |[ {
        |  "Version" : "1.0.0",
        |  "Filename" : "some_file.csv"
        |}, {
        |  "Description" : "description of my other file",
        |  "Version" : "1.2.3",
        |  "Filename" : "some_other_file.csv"
        |} ]""".stripMargin().stripIndent()
    }

}
