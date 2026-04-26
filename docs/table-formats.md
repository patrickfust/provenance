# Table formats

Some generators use tables and needs a formatter to create a string representation of the table.
The table formatter you want to use, is configured when you configure the generator.

## Example
```yaml
- className: dk.fust.provenance.generator.datadict.DataDictionaryConfiguration
  provenanceFile: data-dictionary.yml
  tableFormatter:
    className: dk.fust.provenance.csv.format.table.CSVTableFormatter
  destination:
    className: dk.fust.provenance.destination.FileDestination
    file: data_dictionary-output.csv
```

## MarkdownTableFormatter

Class name: `dk.fust.provenance.format.table.MarkdownTableFormatter`

Default table formatter for most generators.

Generates the table in a format that can be used i Markdown files.

Use it together with [MarkdownDestination](#markdowndestination).

## HTMLTableFormatter

Class name: 'dk.fust.provenance.format.table.HTMLTableFormatter'

| Setting      | Type                | Description                             | Default |
|--------------|---------------------|-----------------------------------------|---------|
| dataFields   | Map<String, String> | Data fields to be appended to the table |         |  
| columnWidths | List<String>        | Setting column withs                    |         |  

### Example

```yaml
tableFormatter:
  className: dk.fust.provenance.format.table.HTMLTableFormatter
  dataFields:
    table-width: "1800"
  columnWidths:
    - "255"
    - "328"
    - "114"
    - "115"
    - "149"
    - "242"
```

## JsonTableFormatter

Class name: 'dk.fust.provenance.format.table.JsonTableFormatter'

Generates JSON.

| Setting     | Type    | Description                                      | Default |
|-------------|---------|--------------------------------------------------|---------|
| yaml        | boolean | If true, it's rendered as yaml otherwise as json | false   |  
| prettyPrint | boolean | Should the json be pretty printed?               | true    |  

### Example

```yaml
tableFormatter:
  className: dk.fust.provenance.format.table.JsonTableFormatter
  yaml: true
```

## CSVTableFormatter

Class name: `dk.fust.provenance.destination.csv.format.table.CSVTableFormatter`

Read the provenance [here](../destinations/provenance-csv)

## ExcelBase64TableFormatter

Class name: `dk.fust.provenance.destination.excel.format.table.ExcelBase64TableFormatter`

Read the provenance [here](../destinations/provenance-excel)
