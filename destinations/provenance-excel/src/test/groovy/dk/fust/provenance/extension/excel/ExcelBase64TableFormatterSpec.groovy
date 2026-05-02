package dk.fust.provenance.extension.excel

import dk.fust.provenance.ProvenanceGenerator
import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestData
import dk.fust.provenance.TestHelper
import dk.fust.provenance.generator.datadict.DataDictionaryConfiguration
import dk.fust.provenance.destination.Base64FileDestination
import dk.fust.provenance.destination.excel.format.table.ExcelBase64TableFormatter
import dk.fust.provenance.destination.excel.format.table.ExcelWorkbookFingerprint
import dk.fust.provenance.format.table.FormatTable
import dk.fust.provenance.service.ProvenanceConfigurationLoaderService
import org.apache.poi.ss.usermodel.IndexedColors
import spock.lang.Specification

class ExcelBase64TableFormatterSpec extends Specification {

    def "generate Excel"() {
        given:
        ExcelBase64TableFormatter excelTableFormatter = new ExcelBase64TableFormatter()
        FormatTable table = TestData.generateTable()
        Base64FileDestination base64FileDestination = new Base64FileDestination(file: new File('target/excel.xlsx'))

        when:
        String excelBase64Encoded = excelTableFormatter.formatTable(table)
        base64FileDestination.sendDocumentToDestination(excelBase64Encoded, null);

        then:
        noExceptionThrown()
        excelBase64Encoded
    }

    def "read using generator configuration"() {
        given:
        ProvenanceConfigurationLoaderService service = new ProvenanceConfigurationLoaderService()

        when:
        List<GeneratorConfiguration> configurations = service.readConfigurations(TestHelper.getTestFile('generator-configuration.yml'))

        then:
        configurations.size() == 1
        configurations[0] instanceof DataDictionaryConfiguration
        DataDictionaryConfiguration ddc = configurations[0] as DataDictionaryConfiguration
        ddc.tableFormatter instanceof ExcelBase64TableFormatter
        ExcelBase64TableFormatter excelBase64TableFormatter = ddc.tableFormatter as ExcelBase64TableFormatter
        excelBase64TableFormatter.headerExcelStyle.fontName == "Verdana"
        excelBase64TableFormatter.headerExcelStyle.fontColor.getIndexedColor() == IndexedColors.WHITE
        excelBase64TableFormatter.headerExcelStyle.backgroundColor.getIndexedColor()
        excelBase64TableFormatter.columnCustomizations.size() == 0

        when: 'generating excel'
        ProvenanceGenerator ProvenanceGenerator = new ProvenanceGenerator();
        ProvenanceGenerator.generate(configurations)

        then:
        noExceptionThrown()
    }

    def "read using generator configuration with column widths"() {
        given:
        ProvenanceConfigurationLoaderService service = new ProvenanceConfigurationLoaderService()

        when:
        List<GeneratorConfiguration> configurations = service.readConfigurations(TestHelper.getTestFile('generator-configuration-column-and-row.yml'))

        then:
        configurations.size() == 1
        DataDictionaryConfiguration ddc = configurations[0] as DataDictionaryConfiguration
        ExcelBase64TableFormatter excelBase64TableFormatter = ddc.tableFormatter as ExcelBase64TableFormatter

        excelBase64TableFormatter.columnCustomizations.size() == 3

        excelBase64TableFormatter.columnCustomizations[0].columnNumber == 0
        excelBase64TableFormatter.columnCustomizations[0].columnDescription == null
        excelBase64TableFormatter.columnCustomizations[0].columnWidth == 20
        excelBase64TableFormatter.columnCustomizations[0].autoResize == null

        excelBase64TableFormatter.columnCustomizations[1].columnNumber == null
        excelBase64TableFormatter.columnCustomizations[1].columnDescription == 'Filename'
        excelBase64TableFormatter.columnCustomizations[1].columnWidth == 30
        excelBase64TableFormatter.columnCustomizations[1].autoResize == null

        excelBase64TableFormatter.columnCustomizations[2].columnNumber == null
        excelBase64TableFormatter.columnCustomizations[2].columnDescription == 'Type'
        excelBase64TableFormatter.columnCustomizations[2].columnWidth == null
        excelBase64TableFormatter.columnCustomizations[2].autoResize == true

        when: 'generating excel'
        ProvenanceGenerator ProvenanceGenerator = new ProvenanceGenerator()
        ProvenanceGenerator.generate(configurations)

        then:
        noExceptionThrown()
    }

    def "content fingerprint is stable across repeated formatting"() {
        given:
        ExcelBase64TableFormatter formatter = new ExcelBase64TableFormatter()
        FormatTable table = TestData.generateTable()

        when:
        String fingerprint1 = formatter.contentFingerprint(table)
        String fingerprint2 = formatter.contentFingerprint(table)

        then:
        fingerprint1 == fingerprint2
    }

    def "content fingerprint changes when table content changes"() {
        given:
        ExcelBase64TableFormatter formatter = new ExcelBase64TableFormatter()
        FormatTable table = TestData.generateTable()

        String originalFingerprint = formatter.contentFingerprint(table)

        when:
        table.rows[2].cells[0].content = "table_a_updated"
        String updatedFingerprint = formatter.contentFingerprint(table)

        then:
        originalFingerprint != updatedFingerprint
    }

    def "content fingerprint changes when style changes"() {
        given:
        ExcelBase64TableFormatter formatter = new ExcelBase64TableFormatter()
        FormatTable table = TestData.generateTable()

        String originalFingerprint = formatter.contentFingerprint(table)

        when: 'header style font size is changed'
        formatter.headerExcelStyle.fontHeightInPoints = 14
        String changedStyleFingerprint = formatter.contentFingerprint(table)

        then:
        originalFingerprint != changedStyleFingerprint
    }

    def "fingerprint is embedded in file and can be read back"() {
        given:
        ExcelBase64TableFormatter formatter = new ExcelBase64TableFormatter()
        FormatTable table = TestData.generateTable()
        File outputFile = new File('target/excel-fingerprinted.xlsx')

        when:
        String expectedFingerprint = formatter.contentFingerprint(table)
        String base64 = formatter.formatTable(table)
        new Base64FileDestination(file: outputFile).sendDocumentToDestination(base64, null)
        Optional<String> embeddedFingerprint = ExcelWorkbookFingerprint.readFromFile(outputFile)

        then:
        embeddedFingerprint.isPresent()
        embeddedFingerprint.get() == expectedFingerprint
    }

}
