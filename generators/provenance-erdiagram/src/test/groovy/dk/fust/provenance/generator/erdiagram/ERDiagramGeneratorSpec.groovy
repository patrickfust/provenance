package dk.fust.provenance.generator.erdiagram

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.MarkdownDestination
import dk.fust.provenance.model.Provenance
import dk.fust.provenance.service.ProvenanceConfigurationLoaderService
import dk.fust.provenance.service.ProvenanceService
import spock.lang.Specification

class ERDiagramGeneratorSpec extends Specification {

    void "generate ER-diagram"() {
        given:
        File markdownFile = File.createTempFile('README', '.md')
        markdownFile << """
Leading stuff

[//]: #SOME_PLACEHOLDER_START ()
SOMETHING TO BE REPLACED
[//]: #SOME_PLACEHOLDER_END ()

Trailing stuff
"""
        ERDiagramConfiguration erDiagramConfiguration = new ERDiagramConfiguration(
                destination: new MarkdownDestination(file: markdownFile),
                generateKeys: [
                        new GenerateKey(filterTags: 'domain-model', destinationKey: 'SOME_PLACEHOLDER')
                ]
        )

        when:
        new ERDiagramGenerator().generate(TestHelper.loadTestProvenance('provenance-erdiagram.yaml'), erDiagramConfiguration)

        then:
        noExceptionThrown()

        and:
        String markdownText = markdownFile.text
        markdownText.contains 'Leading stuff'
        markdownText.contains 'Trailing stuff'
        !markdownText.contains('SOMETHING TO BE REPLACED')
    }

    def "load configuration file with external files"() {
        given:
        File file = TestHelper.getTestFile('provenance-configuration-with-external-files.yml')
        ProvenanceConfigurationLoaderService provenanceConfigurationLoaderService = new ProvenanceConfigurationLoaderService()

        when:
        List<GeneratorConfiguration> configurations = provenanceConfigurationLoaderService.readConfigurations(file)

        then:
        configurations.size() == 1
    }
}
