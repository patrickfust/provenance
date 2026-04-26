package dk.fust.provenance.generator.datalineage

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.MarkdownDestination
import dk.fust.provenance.destination.MockDestination
import dk.fust.provenance.format.table.HTMLTableFormatter
import dk.fust.provenance.format.table.MarkdownTableFormatter
import dk.fust.provenance.format.table.MockTableFormatter
import dk.fust.provenance.service.ProvenanceConfigurationLoaderService
import spock.lang.Specification

class DataLineageGeneratorSpec extends Specification {

    def "generate data lineage"() {
        given:
        MockDestination mockDestination = new MockDestination()
        MockTableFormatter mockTableFormatter = new MockTableFormatter(mockFormat: 'X')
        DataLineageConfiguration configuration = new DataLineageConfiguration(
                key: "myTableClass",
                destination: mockDestination,
                tableFormatter: mockTableFormatter
        )

        DataLineageGenerator generator = new DataLineageGenerator();

        when:
        generator.generate(TestHelper.loadTestProvenance("provenance-datalineage.yaml"), configuration);

        then:
        mockDestination.destination == 'myTableClass'
        mockTableFormatter.formatTableArgument.tableClass == 'myTableClass'
        mockTableFormatter.formatTableArgument.rows.size() == 6
    }

    def 'generate as markdown'() {
        given:
        File markdownFile = new File('target/something.md')
        if (markdownFile.exists()) {
            markdownFile.delete()
        }
        markdownFile << """
[//]: #my-data-lineage_START ()
[//]: #my-data-lineage_END ()
"""
        MarkdownTableFormatter markdownTableFormatter = new MarkdownTableFormatter()
        DataLineageConfiguration conf = new DataLineageConfiguration(
                key: 'my-data-lineage',
                destination: new MarkdownDestination(
                        file: markdownFile,
                ),
                tableFormatter: markdownTableFormatter
        )
        DataLineageGenerator generator = new DataLineageGenerator();

        when:
        generator.generate(TestHelper.loadTestProvenance("provenance-datalineage.yaml"), conf)
        String markdownContent = markdownFile.text

        then:
        markdownContent.contains '--' // The header
    }

    def 'generate as markdown with external source'() {
        given:
        File markdownFile = new File('target/something-external.md')
        if (markdownFile.exists()) {
            markdownFile.delete()
        }
        markdownFile << """
[//]: #my-data-lineage_START ()
[//]: #my-data-lineage_END ()
"""
        MarkdownTableFormatter markdownTableFormatter = new MarkdownTableFormatter()
        DataLineageConfiguration conf = new DataLineageConfiguration(
                key: 'my-data-lineage',
                destination: new MarkdownDestination(
                        file: markdownFile,
                ),
                tableFormatter: markdownTableFormatter,
                sourceProvenanceFiles: ['external' : TestHelper.getTestFile('provenance-datalineage-external-source.yaml')]
        )
        DataLineageGenerator generator = new DataLineageGenerator();

        when:
        generator.generate(TestHelper.loadTestProvenance("provenance-datalineage-with-external-source.yaml"), conf)
        String markdownContent = markdownFile.text

        then:
        markdownContent.contains '--' // The header
    }

    def 'loading configuration'() {
        given:
        ProvenanceConfigurationLoaderService service = new ProvenanceConfigurationLoaderService()
        File file = TestHelper.getTestFile('generator-configuration-with-htmltable.yaml')

        when:
        List<GeneratorConfiguration> conf = service.readConfigurations(file)

        then:
        conf.size() == 1
        conf.first() instanceof DataLineageConfiguration
        DataLineageConfiguration c = conf.first() as DataLineageConfiguration
        c.tableFormatter instanceof HTMLTableFormatter
        HTMLTableFormatter htmlTableFormatter = c.tableFormatter as HTMLTableFormatter
        htmlTableFormatter.columnWidths.size() == 6
        htmlTableFormatter.columnWidths.first() == "255"
    }

}
