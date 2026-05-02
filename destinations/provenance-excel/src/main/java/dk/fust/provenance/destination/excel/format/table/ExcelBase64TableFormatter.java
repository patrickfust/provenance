package dk.fust.provenance.destination.excel.format.table;

import dk.fust.provenance.destination.Base64FileDestination;
import dk.fust.provenance.destination.Destination;
import dk.fust.provenance.destination.excel.format.table.model.ColumnCustomization;
import dk.fust.provenance.destination.excel.format.table.model.ExcelColor;
import dk.fust.provenance.destination.excel.format.table.model.ExcelConfiguration;
import dk.fust.provenance.destination.excel.format.table.model.ExcelStyle;
import dk.fust.provenance.destination.excel.format.table.model.ExcelStyles;
import dk.fust.provenance.format.table.FormatTable;
import dk.fust.provenance.format.table.TableFormatter;
import dk.fust.provenance.util.Assert;
import lombok.Data;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static dk.fust.provenance.destination.excel.format.table.model.ExcelColor.*;

/**
 * Formats a FormatTable into an Excel workbook.
 * The binary content is encoded as base64, and may be written to a file using {@link dk.fust.provenance.destination.Base64FileDestination}
 */
@Data
public class ExcelBase64TableFormatter implements TableFormatter {

    private ExcelStyle headerExcelStyle = new ExcelStyle("Verdana", (short) 11, true, WHITE, DARK_BLUE, WHITE);
    private ExcelStyle secondaryHeaderExcelStyle = new ExcelStyle("Verdana", (short) 9, true, WHITE, LIGHT_BLUE, WHITE);
    private ExcelStyle evenRowExcelStyle = new ExcelStyle("Verdana", (short) 9, false, BLACK, new ExcelColor("220, 220, 220"), WHITE);
    private ExcelStyle oddRowExcelStyle = new ExcelStyle("Verdana", (short) 9, false, BLACK, new ExcelColor("240, 240, 240"), WHITE);

    private int defaultColumnWidth = 50;

    private List<ColumnCustomization> columnCustomizations = new ArrayList<>();

    private boolean autofilter = true;

    private String sheetName = "Sheet1";

    @Override
    public void formatTableAndSendToDestination(FormatTable formatTable, Destination destination, String destinationInDestination) throws IOException {
        try (XSSFWorkbook workbook = FormatTableToExcel.toExcel(formatTable, makeExcelConfiguration())) {
            String fingerprint = ExcelWorkbookFingerprint.fingerprint(workbook);
            if (mustWriteFile(fingerprint, destination)) {
                ExcelWorkbookFingerprint.embedInWorkbook(workbook, fingerprint);
                String excelEncoded = Base64.getEncoder().encodeToString(workbookToBytes(workbook));
                destination.sendDocumentToDestination(excelEncoded, destinationInDestination);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatTable(FormatTable formatTable) {
        try (XSSFWorkbook workbook = FormatTableToExcel.toExcel(formatTable, makeExcelConfiguration())) {
            String fingerprint = ExcelWorkbookFingerprint.fingerprint(workbook);
            ExcelWorkbookFingerprint.embedInWorkbook(workbook, fingerprint);
            return Base64.getEncoder().encodeToString(workbookToBytes(workbook));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean mustWriteFile(String fingerprint, Destination destination) {
        Optional<String> existingFingerprint = readExistingFingerprint(destination);
        return existingFingerprint.map(f -> !f.equals(fingerprint)).orElse(true);
    }

    private Optional<String> readExistingFingerprint(Destination destination) {
        if (destination instanceof Base64FileDestination base64FileDestination) {
            if (base64FileDestination.getFile().exists() && base64FileDestination.getFile().isFile()) {
                return ExcelWorkbookFingerprint.readFromFile(base64FileDestination.getFile());
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a deterministic content fingerprint for change detection.
     */
    public String contentFingerprint(FormatTable formatTable) {
        try (XSSFWorkbook workbook = FormatTableToExcel.toExcel(formatTable, makeExcelConfiguration())) {
            return ExcelWorkbookFingerprint.fingerprint(workbook);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] workbookToBytes(XSSFWorkbook workbook) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        return baos.toByteArray();
    }

    private ExcelConfiguration makeExcelConfiguration() {
        ExcelConfiguration excelConfiguration = new ExcelConfiguration();
        excelConfiguration.setExcelStyles(createExcelStyles());
        excelConfiguration.setDefaultColumnWidth(defaultColumnWidth);
        excelConfiguration.setAutofilter(autofilter);
        excelConfiguration.setColumnCustomizations(columnCustomizations);
        excelConfiguration.setSheetName(sheetName);
        excelConfiguration.validate();
        return excelConfiguration;
    }

    private ExcelStyles createExcelStyles() {
        validateStyle(headerExcelStyle);
        validateStyle(secondaryHeaderExcelStyle);
        validateStyle(evenRowExcelStyle);
        validateStyle(oddRowExcelStyle);
        return new ExcelStyles(headerExcelStyle, secondaryHeaderExcelStyle, oddRowExcelStyle, evenRowExcelStyle);
    }

    private void validateStyle(ExcelStyle excelStyle) {
        Assert.isNotNull(excelStyle, "excelStyle must not be null");
        Assert.isNotNull(excelStyle.getBackgroundColor(), "excelStyle.getBackgroundColor() must not be null");
        Assert.isNotNull(excelStyle.getFontColor(), "excelStyle.getFontColor() must not be null");
        Assert.isTrue(excelStyle.getFontHeightInPoints() > 0, "excelStyle.getFontHeightInPoints() must be greater than 0");
    }

}
