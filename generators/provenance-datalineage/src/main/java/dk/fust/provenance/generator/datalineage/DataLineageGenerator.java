package dk.fust.provenance.generator.datalineage;

import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.format.table.Cell;
import dk.fust.provenance.format.table.Col;
import dk.fust.provenance.format.table.ColGroup;
import dk.fust.provenance.format.table.FormatTable;
import dk.fust.provenance.format.table.Row;
import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.model.Field;
import dk.fust.provenance.model.Generation;
import dk.fust.provenance.model.Table;
import dk.fust.provenance.service.ProvenanceService;
import dk.fust.provenance.util.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generator for Data Lineage
 */
@Slf4j
public class DataLineageGenerator implements Generator {

    private final Map<String, Provenance> keyToExternalProvenance = new HashMap<>();
    private final ProvenanceService provenanceService = new ProvenanceService();

    @Override
    public void generate(Provenance provenance, GeneratorConfiguration generatorConfiguration) throws IOException {
        log.debug("Generating Data lineage...");
        Assert.isTrue(generatorConfiguration instanceof DataLineageConfiguration, "configuration must be of type DataLineageConfiguration");
        DataLineageConfiguration conf = (DataLineageConfiguration) generatorConfiguration;
        FormatTable formatTable = new FormatTable();
        formatTable.setTableClass(conf.getKey());
        formatTable.setColGroup(getColGroup());
        List<Row> rows = new ArrayList<>();
        formatTable.setRows(rows);

        rows.add(getHeaderRow());
        rows.add(getSubHeaderRow());
        rows.addAll(getRows(provenance.filterTables(conf.getFilterTags()), provenance, conf));

        String document = conf.getTableFormatter().formatTable(formatTable);
        generatorConfiguration.getDestination().sendDocumentToDestination(document, conf.getKey());
    }

    private List<Row> getRows(List<Table> tables, Provenance provenance, DataLineageConfiguration configuration) throws IOException {
        List<Row> rows = new ArrayList<>();
        if (tables != null) {
            for (Table table : tables) {
                rows.addAll(getRows(table, provenance, configuration));
            }
        }
        return rows;
    }

    private List<Row> getRows(Table table, Provenance provenance, DataLineageConfiguration configuration) throws IOException {
        List<Row> rows = new ArrayList<>();
        if (table.getFields() != null) {
            for (Field field : table.getFields()) {
                Row row = new Row();
                List<Cell> cells = new ArrayList<>();
                row.setCells(cells);

                cells.add(new Cell(table.getName()));
                cells.add(new Cell(field.getName()));
                cells.add(new Cell(field.getDataType().toLowerCase()));

                if (field.getSource() != null) {
                    addCellsForSource(cells, field.getSource(), provenance, configuration);
                } else {
                    cells.add(new Cell(""));
                    cells.add(new Cell(""));
                    cells.add(new Cell(""));
                    cells.add(new Cell(""));
                }
                if (field.getTransformation() != null) {
                    cells.add(new Cell(field.getTransformation()));
                } else {
                    cells.add(new Cell(""));
                }
                rows.add(row);
            }
        }
        return rows;
    }

    private void addCellsForSource(List<Cell> cells, String source, Provenance provenance, DataLineageConfiguration configuration) throws IOException {
        Provenance sourceProvenance = getSourceFieldProvenance(source, configuration, provenance);
        String[] split = source.split("[.]");
        String tableName = split[split.length - 2];
        String fieldName = split[split.length - 1];
        String database = sourceProvenance.getDatabaseName() != null ? sourceProvenance.getDatabaseName() : "";
        Table sourceTable = sourceProvenance.getTable(tableName);
        Assert.isTrue(sourceTable != null, "Table " + tableName + " not found");
        Generation sourceProvenanceGenerationForTable = sourceProvenance.getGenerationForTable(sourceTable);
        Field sourceField = sourceTable.getField(fieldName, sourceProvenanceGenerationForTable.getGenerateIdDataType(), provenance);
        Assert.isNotNull(sourceField, "sourceField must not be null for source " + source);
        cells.add(new Cell(database));
        cells.add(new Cell(tableName));
        cells.add(new Cell(fieldName));
        cells.add(new Cell(sourceField.getDataType().toLowerCase()));
    }

    private Provenance getSourceFieldProvenance(String source, DataLineageConfiguration conf, Provenance provenance) throws IOException {
        Provenance provenanceToReturn;
        String[] split = source.split("[.]");
        if (split.length == 3) {
            // We're pointing to an external Provenance model file
            String key = split[0];
            if (keyToExternalProvenance.containsKey(key)) {
                provenanceToReturn = keyToExternalProvenance.get(key);
            } else {
                File fileForExternalProvenanceFile = conf.getSourceProvenanceFiles().get(key);
                Assert.isNotNull(fileForExternalProvenanceFile, "source provenance file with key %s can't be found".formatted(key));
                provenanceToReturn = provenanceService.loadProvenance(fileForExternalProvenanceFile);
                Assert.isNotNull(provenanceToReturn, "source provenance file with key %s can't be loaded as Provenance".formatted(key));
                keyToExternalProvenance.put(key, provenanceToReturn);
            }
        } else if (split.length == 2) {
            provenanceToReturn = provenance;
        } else {
            throw new IllegalArgumentException("Length of source may be 2 or 3, not " + split.length + " for source " + source);
        }
        Assert.isNotNull(provenanceToReturn, "Can't find source with name " + source);
        return provenanceToReturn;
    }

    private static ColGroup getColGroup() {
        ColGroup colGroup = new ColGroup();
        colGroup.setCols(List.of(new Col(8)));
        return colGroup;
    }

    private static Row getSubHeaderRow() {
        Row row = new Row();
        row.setCells(List.of(
                new Cell("Table Name"),
                new Cell("Column Name"),
                new Cell("Data Type"),
                new Cell("Database Name"),
                new Cell("Table Name"),
                new Cell("Column Name"),
                new Cell("Data Type")
        ));
        return row;
    }

    private static Row getHeaderRow() {
        Row row = new Row();
        row.setCells(List.of(
                new Cell(3, "Target", true),
                new Cell(4, "Source", true),
                new Cell(1, "Transformation", true)
        ));
        return row;
    }
}
