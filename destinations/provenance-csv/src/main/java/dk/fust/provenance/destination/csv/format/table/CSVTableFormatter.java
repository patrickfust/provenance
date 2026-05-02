package dk.fust.provenance.destination.csv.format.table;

import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.format.table.Cell;
import dk.fust.provenance.format.table.FormatTable;
import dk.fust.provenance.format.table.Row;
import dk.fust.provenance.format.table.TableFormatter;
import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Converts from model of a Table to a CSV representation
 */
@Data
public class CSVTableFormatter implements TableFormatter {

    private CSVDelimiter delimiter = CSVDelimiter.SEMICOLON;
    private CSVRecordSeparator recordSeparator = CSVRecordSeparator.NEWLINE;

    @Override
    public void formatTableAndSendToDestination(FormatTable formatTable, Destination destination, String destinationInDestination) throws IOException {
        destination.sendDocumentToDestination(formatTable(formatTable), destinationInDestination);
    }

    @Override
    public String formatTable(FormatTable formatTable) {
        StringWriter sw = new StringWriter();

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter.getDelimiter())
                .setRecordSeparator(recordSeparator.getRecordSeparator())
                .get();

        try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            for (Row row : formatTable.getRows()) {
                List<String> list = row.getCells().stream().map(Cell::getContent).toList();
                printer.printRecord(list);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not generate CSV-file", e);
        }
        return sw.toString();
    }
}
