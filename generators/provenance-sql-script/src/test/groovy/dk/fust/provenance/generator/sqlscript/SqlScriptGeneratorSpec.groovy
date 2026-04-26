package dk.fust.provenance.generator.sqlscript

import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.DirectoryDestination
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SqlScriptGeneratorSpec extends Specification {

    def "create script for #provenanceFile"() {
        when:
        SqlScriptConfiguration scriptConfiguration = new SqlScriptConfiguration(
                destination: new DirectoryDestination(directory: new File(directory), createParentDirectories: true),
                provenanceFile: new File("src/test/resources/${provenanceFile}")
        )
        scriptConfiguration.validate()
        scriptConfiguration.generator.generate(
                TestHelper.loadTestProvenance(provenanceFile),
                scriptConfiguration
        )

        then:
        noExceptionThrown()

        where:
        directory                      | provenanceFile
        'target/test-scripts'           | 'provenance-sqlscript.yaml'
        'target/test-scripts-no-schema' | 'provenance-sqlscript-no-schema.yaml'
    }

    def "generateId is false"() {
        when:
        File dir = new File('target/test-scripts-generateId')
        SqlScriptConfiguration scriptConfiguration = new SqlScriptConfiguration(
                destination: new DirectoryDestination(directory: dir, createParentDirectories: true),
                provenanceFile: new File("src/test/resources/provenance-generateId.yml")
        )
        scriptConfiguration.validate()
        scriptConfiguration.generator.generate(
                TestHelper.loadTestProvenance("provenance-generateId.yml"),
                scriptConfiguration
        )

        then:
        noExceptionThrown()
        String sql = new File(dir, 'V1__create_table_some_table.sql').text

        and: 'may not have generated an id'
        !sql.contains('some_table_id')
    }
}
