package dk.fust.provenance.generator.sqlscript.generators

import dk.fust.provenance.TestHelper
import dk.fust.provenance.destination.DirectoryDestination
import dk.fust.provenance.model.Provenance
import dk.fust.provenance.generator.sqlscript.SqlScriptConfiguration
import spock.lang.Specification

class PostgresGeneratorSpec extends Specification {

    def "generate without indexes"() {
        when:
        generate('provenance-sqlscript.yaml', 'target/test-scripts-postgres')

        then:
        noExceptionThrown()
    }

    def "generate with index comment"() {
        setup:
        String outputDirectory = 'target/test-scripts-postgres-index-comment'

        when:
        generate('provenance-sqlscript-with-index-comment.yaml', outputDirectory)

        then:
        noExceptionThrown()
        String indexFileText = new File(outputDirectory, 'create-index_with_comment.sql').text
        indexFileText.contains("comment on index xxx.nonUniqueIdx is 'a comment on nonUniqueIdx';")
        indexFileText.contains("comment on index xxx.uniqueIdx is 'a comment on uniqueIdx';")
    }

    def "generate with integer array"() {
        setup:
        String outputDirectory = 'target/test-scripts-postgres-integer-array'

        when:
        generate('provenance-sqlscript-integer-array.yaml', outputDirectory)

        then:
        noExceptionThrown()
        String indexFileText = new File(outputDirectory, 'create-something-with-integer-array.sql').text
        indexFileText.contains("name int[]")
    }

    def "generate with foreign keys"() {
        setup:
        String outputDirectory = 'target/test-scripts-postgres-foreign-keys'

        when:
        generate('provenance-sqlscript-foreign-keys.yaml', outputDirectory)

        then:
        noExceptionThrown()
        new File(outputDirectory, 'plain.sql').text.contains("parent text references parent_table(key),")
        new File(outputDirectory, 'delete_cascade.sql').text.contains("parent text references parent_table(key) on delete cascade,")
        new File(outputDirectory, 'update_cascade.sql').text.contains("parent text references parent_table(key) on update cascade,")
        new File(outputDirectory, 'mixed.sql').text.contains("parent text references parent_table(key) on update set null on delete set default,")
        new File(outputDirectory, 'combined.sql').text.contains("foreign key(pointer_to_parent_key, pointer_to_parent_name) references parent_table(key, name) on update cascade on delete restrict")
        new File(outputDirectory, 'combined.sql').text.contains("foreign key(pointer_to_parent_key) references child_table_mixed(parent)")
    }

    def "generate with collate"() {
        setup:
        String outputDirectory = 'target/test-scripts-collate'

        when:
        generate('provenance-sqlscript-collate.yaml', outputDirectory)

        then:
        noExceptionThrown()
        String someTable = new File(outputDirectory, 'some_table.sql').text
        someTable.contains('field_with_default_collate text,')
        someTable.contains('field_with_specific_collate text collate "C"')
    }

    private void generate(String provenanceFile, String outputDirectory) {
        PostgresGenerator generator = new PostgresGenerator()
        Provenance provenance = TestHelper.loadTestProvenance(provenanceFile)
        SqlScriptConfiguration scriptConfiguration = new SqlScriptConfiguration(
                destination: new DirectoryDestination(directory: new File(outputDirectory), createParentDirectories: true),
                provenanceFile: new File("src/test/resources/$provenanceFile")
        )
        scriptConfiguration.validate()
        generator.generate(provenance, scriptConfiguration)
    }

}
