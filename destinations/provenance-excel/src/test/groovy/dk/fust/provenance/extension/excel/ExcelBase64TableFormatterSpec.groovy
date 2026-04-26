package dk.fust.provenance.destination.excel

import dk.fust.provenance.ProvenanceGenerator
import dk.fust.provenance.GeneratorConfiguration
import dk.fust.provenance.TestData
import dk.fust.provenance.TestHelper
import dk.fust.provenance.generator.datadict.DataDictionaryConfiguration
import dk.fust.provenance.destination.Base64FileDestination
import dk.fust.provenance.destination.excel.format.table.ExcelBase64TableFormatter
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

}
