package dk.fust.provenance.service

import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.MockGeneratorConfiguration
import dk.fust.provenance.TestHelper
import dk.fust.provenance.model.Provenance
import spock.lang.Specification

class ProvenanceServiceSpec extends Specification {

    def "sunshine"() {
        given:
        ProvenanceService provenanceService = new ProvenanceService()
        File testFile = TestHelper.getTestFile('provenance-test.yaml')

        when:
        Provenance provenance = provenanceService.loadProvenance(testFile)

        then:
        noExceptionThrown()
        provenance
    }

    def "read json file"() {
        given:
        ProvenanceService provenanceService = new ProvenanceService()
        File testFile = TestHelper.getTestFile('provenance-test.json')

        when:
        Provenance provenance = provenanceService.loadProvenance(testFile)

        then:
        noExceptionThrown()
        provenance.provenanceTitle == 'The provenance title in a JSON file'
    }

    def "sunshine from configuration with external files"() {
        given:
        ProvenanceService provenanceService = new ProvenanceService()
        File testFile = TestHelper.getTestFile('provenance-needs-external.yaml')
        GeneratorConfiguration generatorConfiguration = new MockGeneratorConfiguration(
                externalProvenanceFiles: [testFile.getAbsolutePath()]
        )

        when:
        List<Provenance> provenanceFiles = provenanceService.loadExternalProvenanceFiles(generatorConfiguration)

        then:
        noExceptionThrown()
        provenanceFiles.size() == 1
    }

}
