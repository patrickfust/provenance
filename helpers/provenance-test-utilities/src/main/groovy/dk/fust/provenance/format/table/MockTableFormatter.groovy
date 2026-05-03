package dk.fust.provenance.format.table

import dk.fust.provenance.destination.Destination

class MockTableFormatter implements TableFormatter {

    String mockFormat
    FormatTable formatTableArgument

    @Override
    String formatTable(FormatTable formatTable) {
        this.formatTableArgument = formatTable
        return mockFormat
    }

    @Override
    void formatTableAndSendToDestination(FormatTable table, Destination destination, String destinationInDestination) {
        destination.sendDocumentToDestination(formatTable(table), destinationInDestination)
    }

}
