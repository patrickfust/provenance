package dk.fust.provenance

import dk.fust.provenance.destination.MockDestination
import spock.lang.Specification

class ProvenanceGeneratorSpec extends Specification {

    def "generate provenance"() {
        given:
        Generator generator = Mock(Generator) {
            1 * generate(_, _)
        }
        GeneratorConfiguration documentationConfiguration = Mock(GeneratorConfiguration) {
            1 * getProvenanceFile() >> {
                return TestHelper.getTestFile('provenance-test.yaml')
            }
            1 * getGenerator() >> {
                return generator
            }
            _ * getDestination() >> {
                return new MockDestination()
            }
        }
        ProvenanceGenerator documentationGenerator = new ProvenanceGenerator()

        when:
        documentationGenerator.generate(documentationConfiguration)

        then:
        noExceptionThrown()
    }

}
