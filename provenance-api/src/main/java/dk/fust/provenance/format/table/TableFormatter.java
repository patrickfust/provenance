package dk.fust.provenance.format.table;

import dk.fust.provenance.destination.Destination;

import java.io.IOException;

/**
 * How do you generate a table?
 * This could be in the form of HTML or Markdown for instance
 */
public interface TableFormatter {

    /**
     * Convert the table to a string that represent the table
     * @param formatTable table to convert
     * @return a string representing the table
     */
    String formatTable(FormatTable formatTable);

    /**
     * Convert the table to a string that represent the table and sends it to the destination if applicable
     * @param formatTable table to convert
     * @param destination where the table should be sent to
     * @param destinationInDestination where the table should be sent to in the destination
     */
    void formatTableAndSendToDestination(FormatTable formatTable, Destination destination, String destinationInDestination) throws IOException;

}
