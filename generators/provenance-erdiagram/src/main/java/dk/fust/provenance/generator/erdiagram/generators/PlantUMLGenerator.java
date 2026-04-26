package dk.fust.provenance.generator.erdiagram.generators;

import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.model.Field;
import dk.fust.provenance.model.Generation;
import dk.fust.provenance.model.Table;

import java.util.List;

/**
 * Generates ER-diagram in plantuml format
 */
public class PlantUMLGenerator implements ERGenerator {
    private static final String PROCEDURES = """
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
!endprocedure""";

    @Override
    public String getMarkdownType() {
        return "plantuml";
    }

    @Override
    public String generateUML(String filterTags, Provenance provenance, GeneratorConfiguration generatorConfiguration) {
        StringBuilder stringBuilder = new StringBuilder(1024);
        List<Table> tables = provenance.filterTables(filterTags);
        stringBuilder.append("""
@startuml

!theme plain
hide empty methods

%s
""".formatted(PROCEDURES));
        if (provenance.getProvenanceTitle() != null) {
            stringBuilder.append("title \"%s\"\n".formatted(provenance.getProvenanceTitle()));
        }
        if (provenance.getSchemaName() != null) {
            stringBuilder.append("$schema(\"%s\") {\n".formatted(provenance.getSchemaName()));
        }
        stringBuilder.append(generateTables(tables, provenance));
        if (provenance.getSchemaName() != null) {
            stringBuilder.append("}\n");
        }
        stringBuilder.append(generateForeignKeys(tables, provenance));
        stringBuilder.append("@enduml");
        return stringBuilder.toString();
    }

    private String generateTables(List<Table> tables, Provenance provenance) {
        StringBuilder uml = new StringBuilder(128);
        for (Table table : tables) {
            uml.append(generateTable(table, provenance));
        }
        return uml.toString();
    }

    private static String generateTable(Table table, Provenance provenance) {
        StringBuilder uml = new StringBuilder(128);
        uml.append("  $table(\"%s\") {\n".formatted(table.getName()));
        Generation generationForTable = provenance.getGenerationForTable(table);
        if (generationForTable.isGenerateId()) {
            uml.append("    $pk(\"%s_id\"): %s NOT NULL\n".formatted(table.getName(), generationForTable.getGenerateIdDataType().toLowerCase()));
        }
        table.getFields().forEach(field -> {
            if (table.isFieldForeignKey(field.getName(), provenance)) {
                uml.append(key("fk", field));
            } else if (field.isPrimaryKey()) {
                uml.append(key("pk", field));
            } else {
                uml.append(key("column", field));
            }
        });
        uml.append("  }\n");
        return uml.toString();
    }

    private static String key(String key, Field field) {
        return "    $%s(\"%s\"): %s\n".formatted(key, field.getName(), field.getDataType().toLowerCase());
    }

    private static String generateForeignKeys(List<Table> tables, Provenance provenance) {
        StringBuilder uml = new StringBuilder(128);
        tables.forEach(table -> {
            table.getFields().forEach(field -> {
                if (field.getForeignKey() != null) {
                    uml.append(generateForeignKey(provenance.getSchemaName(),
                            table.getName(), field.getName(),
                            field.getForeignKey().getTableName(), field.getForeignKey().getColumnName()));
                }
            });
            if (table.getForeignKeys() != null) {
                table.getForeignKeys().forEach(combinedForeignKey -> {
                    combinedForeignKey.getColumns().forEach(column -> {
                        uml.append(generateForeignKey(provenance.getSchemaName(),
                                table.getName(), column.getReferencingColumn(),
                                combinedForeignKey.getTableName(), column.getReferenceColumn()));
                    });
                });
            }
        });
        return uml.toString();
    }

    private static String generateForeignKey(String schemaName, String referencingTableName, String referencingColumn, String referenceTableName, String referenceColumn) {
        if (schemaName != null) {
            return "%s.%s::%s ||--o{ %s.%s::%s\n".formatted(
                    schemaName, referenceTableName, referenceColumn,
                    schemaName, referencingTableName, referencingColumn
            );
        } else {
            return "%s::%s ||--o{ %s::%s\n".formatted(
                    referenceTableName, referenceColumn,
                    referencingTableName, referencingColumn
            );
        }
    }
}
