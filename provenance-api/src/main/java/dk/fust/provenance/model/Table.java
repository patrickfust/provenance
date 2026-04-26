package dk.fust.provenance.model;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * Definition of a database table
 */
@Data
public class Table {

    private String name;

    @Description("Comment to be added to the table")
    private String comment;

    @Description("List of tags that may be used in a filter")
    private List<String> tags;

    @Description(value = "Filename of sql-script-file to be generated. If left out, no files are generated")
    private String createTableScript;

    private List<Field> fields;

    private List<View> views;

    private List<Index> indexes;

    private List<CombinedForeignKey> foreignKeys;

    @Description("Configuration on how the generation should appear - Overrides the general generation on `Provenance`")
    private Generation generation;

    /**
     * Retrieves the found field. If it's a generated field, it's synthesized.
     * @param fieldName name to find
     * @param generatedIdDataType if it's a generated field, this will be the data type
     * @param provenance reference to the complete model
     * @return found field or null if it don't exist
     */
    public Field getField(String fieldName, DataType generatedIdDataType, Provenance provenance) {
        Field foundField = fields.stream().filter(field -> field.getName().equals(fieldName)).findFirst().orElse(null);
        if (foundField == null) {
            boolean isGeneratedId = Objects.equals(fieldName, name + "_id");
            if (provenance.getGenerationForTable(this).isGenerateId() && isGeneratedId) {
                // Synthesized generated id field
                foundField = new Field();
                foundField.setName(fieldName);
                foundField.setDataType(generatedIdDataType);
            }
        }
        return foundField;
    }

    /**
     * A field is considered foreign key if it has a foreign key definition or if it is a part in the list of foreign keys on the table level.
     * @param fieldName fieldName to search for
     * @param provenance reference to the complete model
     * @return true if it is a foreign key
     */
    public boolean isFieldForeignKey(String fieldName, Provenance provenance) {
        Field field = getField(fieldName, null, provenance);
        if (field != null && field.getForeignKey() != null) {
            return true;
        }
        if (getForeignKeys() != null) {
            for (CombinedForeignKey foreignKey : getForeignKeys()) {
                for (CombinedForeignKeyColumn column : foreignKey.getColumns()) {
                    if (fieldName.equals(column.getReferencingColumn())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
