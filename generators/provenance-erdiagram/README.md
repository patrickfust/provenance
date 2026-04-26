# Provenance Generator ER-diagram

This generator can generate entity-relation diagrams in the format of [PlantUML](https://plantuml.com/) and [Mermaid](https://mermaid.js.org/)

## Configuration

To use the entity-relation diagram generator, you must configure it with `dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration`

| Setting           | 	Type             | Description                                                        | 	Default |
|-------------------|-------------------|--------------------------------------------------------------------|----------|
| provenanceFile | File              | Location of the provenance file                                 |          |
| destination       | Destination       | Where to send the generated provenance                          |          | 
| umlGenerator      | UMLGenerator      | Which type of ER-diagram to generate. Can be MERMAID and PLANTUML  | MERMAID  |
| generateKeys      | List<GenerateKey> | List of model-tags and the placeholder to replace in provenance |          | 

### GenerateKey

`GenerateKey` identifies a sub-set of the tables to generate an ER-diagram for.
You can group them for instance into domain-model and external-reference and so forth, by adding a filter.
Only tables with a tag matching the filter will be used.

If the `filterTags` is `null`, then all tables are selected.

The `destinationKey` tells which part of the destination that needs replacing.

For instance if you're using `MarkdownDestination`, you can point to the same markdown-file but with different `destinationKey`, 
so you can have several ER-diagram with a sub-set of the tables.

Read more about `MarkdownDestination` [here](../../README.md#dkfustdocgendestinationmarkdowndestination-)

## Model usage

This generator uses these fields in [Provenance](../../provenance-generator-api/src/main/java/dk/fust/docgen/model/Provenance.java)

```
Provenance
├── provenanceTitle
├── schemaName
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
    ├── comment
    ├── tags
    ├── foreignKeys
    │   ├── tableName
    │   └── columns
    │       ├── referencingColumn
    │       └── referenceColumn
    └── fields
        ├── name
        ├── dataType
        ├── foreignKey
        │   ├── tableName
        │   └── columnName
        └── primaryKey
```

---

## Examples

### gradle.build

```groovy
import dk.fust.provenance.generator.erdiagram.GenerateKey
import generators.dk.fust.provenance.generator.erdiagram.UMLGenerator
import dk.fust.provenance.destination.MarkdownDestination
import dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration

new ERDiagramConfiguration(
        provenanceFile: new File(projectDir, 'provenance.yml'),
        umlGenerator: UMLGenerator.MERMAID, // Can be omitted because it's default
        generateKeys: [
                // Empty filter means all groups
                new GenerateKey(destinationKey: 'MODEL_MERMAID_PLACEHOLDER'),
                new GenerateKey(destinationKey: 'MODEL_MERMAID_GROUP_PLACEHOLDER', filterTags: 'my_group')
        ],
        destination: new MarkdownDestination(
                file: new File('README.md'),
        )
)
```

### generator-configuration.yml

```yaml
- className: dk.fust.provenance.generator.erdiagram.ERDiagramConfiguration
  provenanceFile: provenance.yml
  umlGenerator: MERMAID
  generateKeys:
    - className: dk.fust.provenance.generator.erdiagram.GenerateKey
      destinationKey: MODEL_MERMAID_PLACEHOLDER
    - className: dk.fust.provenance.generator.erdiagram.GenerateKey
      destinationKey: MODEL_MERMAID_GROUP_PLACEHOLDER
      filterTags: my_group
  destination:
    className: dk.fust.provenance.destination.MarkdownDestination
    file: README.md
```

## Demo

See [provenance-demos](https://github.com/patrickfust/provenance-demos) for demo.
