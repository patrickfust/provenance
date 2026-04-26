# Provenance Generator Data lineage

This generator can generate [data lineage](https://www.ibm.com/topics/data-lineage).

## Configuration

To use the entity-relation diagram generator, you must configure it with `dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration`

| Setting                  | 	Type             | Description                                                                       | Default                  |
|--------------------------|-------------------|-----------------------------------------------------------------------------------|--------------------------|
| provenanceFile        | File              | Location of the provenance file                                                |                          |
| destination              | Destination       | Where to send the generated provenance                                         |                          | 
| key                      | String            | Key to identify it on the destination                                             |                          |
| sourceProvenanceFiles | Map<String, File> | External references to other provenancefiles. Is used when specifying `source` |                          |
| tableFormatter           | TableFormatter    | Formatter that can create the table                                               | `MarkdownTableFormatter` |
| filterTags               | String            | If specified, only tables with this filter is used                                |                          | 

## Model usage

This generator uses these fields in [Provenance](../../provenance-generator-api/src/main/java/dk/fust/docgen/model/Provenance.java)

```
Provenance
├── databaseName
├── generation
│   ├── generateIdDataType
│   ├── generateId
│   ├── addCreatedAt
│   ├── columnNameCreatedAt
│   ├── addUpdatedAt
│   ├── columnNameUpdatedAt
│   └── triggerForUpdates
└── tables
    ├── generation (same sub fields as above)
    ├── name
    ├── tags
    └── fields
        ├── name
        ├── comment
        ├── transformation
        ├── source
        └── dataType
```

---

## Examples

### gradle.build

```groovy
import dk.fust.provenance.generator.datalineage.DataLineageConfiguration
import dk.fust.provenance.destination.MarkdownDestination
import dk.fust.provenance.format.table.MarkdownTableFormatter

new DataLineageConfiguration(
        key: 'my-data-lineage',
        provenanceFile: new File(projectDir, 'provenance.yml'),
        destination: new MarkdownDestination(
                file: new File('README.md'),
        ),
        tableFormatter: new MarkdownTableFormatter(),
        sourceProvenanceFiles: [
                'external' : new File(projectDir, 'external-provenance.yaml')
                'external2': new File(projectDir, 'external2-provenance.yaml')
        ]
)
```

### generator-configuration.yml

```yaml
- className: dk.fust.provenance.generator.datalineage.DataLineageConfiguration
  provenanceFile: provenance.yml
  key: my-data-lineage
  tableFormatter:
    className: dk.fust.provenance.format.table.MarkdownTableFormatter
  destination:
    className: dk.fust.provenance.destination.MarkdownDestination
    file: README.md
```

---

## Demo

See [provenance-demos](https://github.com/patrickfust/provenance-demos) for demo.
