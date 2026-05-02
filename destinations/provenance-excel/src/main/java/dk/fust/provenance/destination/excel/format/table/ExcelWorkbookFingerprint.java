package dk.fust.provenance.destination.excel.format.table;

import dk.fust.provenance.util.Assert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Computes a deterministic fingerprint from workbook content.
 * This intentionally ignores volatile ZIP/package metadata that can change between writes.
 * The fingerprint can be embedded as a custom document property via {@link #embedInWorkbook(XSSFWorkbook, String)}
 * and later retrieved from a saved file via {@link #readFromFile(File)} or from an open workbook
 * via {@link #readFromWorkbook(XSSFWorkbook)}.
 */
public final class ExcelWorkbookFingerprint {

    static final String PROVENANCE_FINGERPRINT = "ProvenanceFingerprint";

    private static final int FORMAT_VERSION = 1;

    /**
     * Embeds the given fingerprint string as a custom document property on the workbook.
     * Call this before writing the workbook to a stream/file.
     *
     * @param workbook    target workbook
     * @param fingerprint fingerprint string to embed
     */
    public static void embedInWorkbook(XSSFWorkbook workbook, String fingerprint) {
        Assert.isNotNull(workbook, "workbook must not be null");
        Assert.isNotNull(fingerprint, "fingerprint must not be null");
        workbook.getProperties().getCustomProperties().addProperty(PROVENANCE_FINGERPRINT, fingerprint);
    }

    /**
     * Reads the embedded fingerprint from an Excel file, if present.
     *
     * @param file the .xlsx file to read
     * @return the fingerprint, or {@link Optional#empty()} if no fingerprint is embedded
     */
    public static Optional<String> readFromFile(File file) {
        Assert.isNotNull(file, "file must not be null");
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            return readFromWorkbook(workbook);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read fingerprint from file: " + file, e);
        }
    }

    /**
     * Reads the embedded fingerprint from an already-open workbook, if present.
     *
     * @param workbook the workbook to inspect
     * @return the fingerprint, or {@link Optional#empty()} if no fingerprint is embedded
     */
    public static Optional<String> readFromWorkbook(XSSFWorkbook workbook) {
        Assert.isNotNull(workbook, "workbook must not be null");
        var customProps = workbook.getProperties().getCustomProperties();
        if (customProps.contains(PROVENANCE_FINGERPRINT)) {
            return Optional.ofNullable(customProps.getProperty(PROVENANCE_FINGERPRINT).getLpwstr());
        }
        return Optional.empty();
    }

    public static String fingerprint(XSSFWorkbook workbook) {
        Assert.isNotNull(workbook, "workbook must not be null");

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            try (DigestOutputStream digestOutputStream = new DigestOutputStream(OutputStream.nullOutputStream(), messageDigest);
                 DataOutputStream out = new DataOutputStream(digestOutputStream)) {
                out.writeInt(FORMAT_VERSION);

                // Write workbook properties
                writeNullableString(out, workbook.getProperties().getCoreProperties().getTitle());
                writeNullableString(out, workbook.getProperties().getCoreProperties().getCreator());
                writeNullableString(out, workbook.getProperties().getCoreProperties().getCategory());
                writeNullableString(out, workbook.getProperties().getCoreProperties().getDescription());
                writeNullableString(out, workbook.getProperties().getCoreProperties().getSubject());
                writeNullableString(out, workbook.getProperties().getCoreProperties().getKeywords());
                writeNullableString(out, workbook.getProperties().getExtendedProperties().getCompany());

                out.writeInt(workbook.getNumberOfSheets());
                for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
                    writeSheet(workbook.getSheetAt(sheetIdx), out);
                }
                out.flush();
            }
            return toHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Unable to create workbook fingerprint", e);
        }
    }

    private static void writeSheet(XSSFSheet sheet, DataOutputStream out) throws IOException {
        writeString(out, sheet.getSheetName());
        out.writeInt(sheet.getDefaultColumnWidth());

        String autoFilterRef = sheet.getCTWorksheet().isSetAutoFilter()
                ? sheet.getCTWorksheet().getAutoFilter().getRef()
                : null;
        writeNullableString(out, autoFilterRef);

        List<CellRangeAddress> mergedRegions = getSortedMergedRegions(sheet);
        out.writeInt(mergedRegions.size());
        for (CellRangeAddress mergedRegion : mergedRegions) {
            out.writeInt(mergedRegion.getFirstRow());
            out.writeInt(mergedRegion.getLastRow());
            out.writeInt(mergedRegion.getFirstColumn());
            out.writeInt(mergedRegion.getLastColumn());
        }

        int lastRowNum = sheet.getLastRowNum();
        out.writeInt(lastRowNum);

        int maxSeenColumn = 0;
        for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            out.writeBoolean(row != null);
            if (row == null) {
                continue;
            }

            int lastCellNum = Math.max(0, row.getLastCellNum());
            out.writeInt(lastCellNum);
            for (int colNum = 0; colNum < lastCellNum; colNum++) {
                Cell cell = row.getCell(colNum, org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                out.writeBoolean(cell != null);
                if (cell == null) {
                    continue;
                }

                maxSeenColumn = Math.max(maxSeenColumn, colNum);
                writeCellStyle(out, (XSSFCellStyle) cell.getCellStyle());
                writeCellValue(out, cell);
            }
        }

        out.writeInt(maxSeenColumn);
        for (int colNum = 0; colNum <= maxSeenColumn; colNum++) {
            out.writeInt(sheet.getColumnWidth(colNum));
            out.writeBoolean(sheet.isColumnHidden(colNum));
        }
    }

    private static void writeCellStyle(DataOutputStream out, XSSFCellStyle style) throws IOException {
        // Font
        XSSFFont font = style.getFont();
        writeNullableString(out, font.getFontName());
        out.writeShort(font.getFontHeightInPoints());
        out.writeBoolean(font.getBold());
        out.writeBoolean(font.getItalic());
        out.writeBoolean(font.getStrikeout());
        out.writeShort(font.getColor());
        writeNullableXSSFColor(out, font.getXSSFColor());

        // Fill
        out.writeShort(style.getFillForegroundColor());
        writeNullableXSSFColor(out, style.getFillForegroundXSSFColor());
        out.writeInt(style.getFillPattern().getCode());

        // Alignment
        out.writeInt(style.getAlignment().getCode());
        out.writeInt(style.getVerticalAlignment().getCode());
        out.writeBoolean(style.getWrapText());

        // Borders
        out.writeInt(style.getBorderTop().getCode());
        out.writeInt(style.getBorderBottom().getCode());
        out.writeInt(style.getBorderLeft().getCode());
        out.writeInt(style.getBorderRight().getCode());
        out.writeShort(style.getTopBorderColor());
        out.writeShort(style.getBottomBorderColor());
        out.writeShort(style.getLeftBorderColor());
        out.writeShort(style.getRightBorderColor());
    }

    private static void writeNullableXSSFColor(DataOutputStream out, XSSFColor color) throws IOException {
        out.writeBoolean(color != null);
        if (color != null) {
            byte[] rgb = color.getRGB();
            out.writeBoolean(rgb != null);
            if (rgb != null) {
                out.writeInt(rgb.length);
                out.write(rgb);
            }
        }
    }

    private static void writeCellValue(DataOutputStream out, Cell cell) throws IOException {
        CellType cellType = cell.getCellType();
        out.writeInt(cellType.ordinal());
        switch (cellType) {
            case STRING -> writeNullableString(out, cell.getStringCellValue());
            case NUMERIC -> out.writeLong(Double.doubleToLongBits(cell.getNumericCellValue()));
            case BOOLEAN -> out.writeBoolean(cell.getBooleanCellValue());
            case FORMULA -> {
                writeNullableString(out, cell.getCellFormula());
                writeFormulaCachedValue(out, cell);
            }
            case ERROR -> out.writeByte(cell.getErrorCellValue());
            case BLANK, _NONE -> {
                // No extra payload for blank/no-type cells.
            }
        }
    }

    private static void writeFormulaCachedValue(DataOutputStream out, Cell cell) throws IOException {
        CellType cachedType = cell.getCachedFormulaResultType();
        out.writeInt(cachedType.ordinal());
        switch (cachedType) {
            case STRING -> writeNullableString(out, cell.getStringCellValue());
            case NUMERIC -> out.writeLong(Double.doubleToLongBits(cell.getNumericCellValue()));
            case BOOLEAN -> out.writeBoolean(cell.getBooleanCellValue());
            case ERROR -> out.writeByte(cell.getErrorCellValue());
            case BLANK, _NONE, FORMULA -> {
                // Nothing else needed for these cached types.
            }
        }
    }

    private static List<CellRangeAddress> getSortedMergedRegions(XSSFSheet sheet) {
        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        for (int idx = 0; idx < sheet.getNumMergedRegions(); idx++) {
            mergedRegions.add(sheet.getMergedRegion(idx));
        }

        mergedRegions.sort(
                Comparator.comparingInt(CellRangeAddress::getFirstRow)
                        .thenComparingInt(CellRangeAddress::getLastRow)
                        .thenComparingInt(CellRangeAddress::getFirstColumn)
                        .thenComparingInt(CellRangeAddress::getLastColumn)
        );
        return mergedRegions;
    }

    private static void writeNullableString(DataOutputStream out, String value) throws IOException {
        out.writeBoolean(value != null);
        if (value != null) {
            writeString(out, value);
        }
    }

    private static void writeString(DataOutputStream out, String value) throws IOException {
        out.writeInt(value.length());
        out.writeChars(value);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}


