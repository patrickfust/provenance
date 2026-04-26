package dk.fust.provenance.generator.erdiagram.generators;

import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.model.Field;
import dk.fust.provenance.model.Generation;
import dk.fust.provenance.model.Table;

import java.util.List;
import java.util.stream.Stream;

/**
 * Generates ER-diagram in mermaid format
 */
public class MermaidGenerator implements ERGenerator {

    private static final int INITIAL_CAPACITY = 512;

    @Override
    public String getMarkdownType() {
        return "mermaid";
    }

    @Override
    public String generateUML(String filterTags, Provenance provenance, GeneratorConfiguration generatorConfiguration) {
        StringBuilder uml = new StringBuilder(INITIAL_CAPACITY);
        if (provenance.getProvenanceTitle() != null && !provenance.getProvenanceTitle().isEmpty()) {
            uml.append("""
---
title: %s
---
""".formatted(provenance.getProvenanceTitle()));
        }
        uml.append("erDiagram\n");
        String tables = generateTables(filterTags, provenance);
        uml.append(tables);
        return uml.toString();
    }

    private String generateTables(String filterTags, Provenance provenance) {
        StringBuilder uml = new StringBuilder(INITIAL_CAPACITY);

        List<Table> tables = provenance.filterTables(filterTags);
        tables.forEach(table -> {
            uml.append(generateTableForeignKeys(table));

            uml.append("%s {\n".formatted(table.getName()));
            Generation generationForTable = provenance.getGenerationForTable(table);
            if (generationForTable.isGenerateId()) {
                uml.append("    %s %s_id\n".formatted(generationForTable.getGenerateIdDataType().toLowerCase(), table.getName()));
            }
            if (table.getFields() != null) {
                table.getFields().forEach(field -> {
                    uml.append("    %s %s".formatted(field.getDataType(), field.getName()));
                    if (field.isPrimaryKey()) {
                        uml.append(" PK");
                    }
                    if (table.isFieldForeignKey(field.getName(), provenance)) {
                        if (field.isPrimaryKey()) {
                            uml.append(", FK");
                        } else {
                            uml.append(" FK");
                        }
                    }
                    uml.append("\n");
                });
            }
            uml.append("}\n");
        });

        return uml.toString();
    }

    private String generateTableForeignKeys(Table table) {
        StringBuilder uml = new StringBuilder();
        if (table.getFields() != null) {
            Stream<Field> foreignKeys = table.getFields().stream().filter(t -> t.getForeignKey() != null);
            foreignKeys.forEach(foreignKey -> {
                uml.append(generateForeignKey(foreignKey.getForeignKey().getTableName(), table.getName()));
            });
            if (table.getForeignKeys() != null) {
                table.getForeignKeys().forEach(combinedForeignKey -> {
                    uml.append(generateForeignKey(combinedForeignKey.getTableName(), table.getName()));
                });
            }
        }
        return uml.toString();
    }

    private String generateForeignKey(String referencingTableName, String referenceTableName) {
        return "    %s ||--o{ %s : \"\"\n".formatted(
                referencingTableName,
                referenceTableName);
    }

}
