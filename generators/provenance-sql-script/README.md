# Provenance Generator SQL Scripts

This generator can generate sql files containing create tables, indexes and views.

## Configuration

To use the sql generator, you must configure it with `dk.fust.provenance.generator.sqlscript.SqlScriptConfiguration`

| Setting           | 	Type       | Description                                        | Default  |
|-------------------|-------------|----------------------------------------------------|----------|
| provenanceFile | File        | Location of the provenance file                 |          |
| destination       | Destination | Where to send the generated provenance          |          | 
| filterTags        | String      | If specified, only tables with this filterTags is used |          |
| sqlDialect        | SqlDialect  | SQL Dialect that the sql scripts are generated in  | POSTGRES |

## Model usage

This generator uses these fields in [Provenance](../../provenance-generator-api/src/main/java/dk/fust/docgen/model/Provenance.java)

```
Provenance
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
    ├── createTableScript
    ├── fields
    │   ├── name
    │   ├── comment
    │   ├── dataType
    │   ├── foreignKey
    │   │   ├── tableName
    │   │   ├── columnName
    │   │   ├── onDelete
    │   │   ├── onUpdate
    │   │   └── enforceReference
    │   ├── primaryKey
    │   ├── collate
    │   ├── unique
    │   ├── nullable
    │   ├── defaultValue
    │   └── check
    ├── foreignKeys
    │   ├── tableName
    │   ├── columns
    │   │   ├── referencingColumn
    │   │   └── referenceColumn
    │   ├── onDelete
    │   ├── onUpdate
    │   └── enforceReference
    ├── views
    │   ├── name
    │   └── sql 
    └── indexes
        ├── name
        ├── unique
        └── fields
```

---
## Foreign Keys

If you want to use foreign keys, you must specify the `foreignKey` field in either the table field or on the table level.

### Foreign key on field level
The `foreignKey` on a field, is an object with the following fields:

| Field            | Type    | Description                                                                                                     | Default     |
|------------------|---------|-----------------------------------------------------------------------------------------------------------------|-------------|
| tableName        | String  | Name of the table that the foreign key references                                                               |             |
| columnName       | String  | Name of the column that the foreign key references                                                              |             |
| enforceReference | Boolean | Whether to enforce the foreign key reference                                                                    |             |
| onDelete         | String  | [Action](#actions) to take when the referenced row is deleted. <br/>Only active if `enforceReference` is `true` | `no_action` |
| onUpdate         | String  | [Action](#actions) to take when the referenced row is updated  <br/>Only active if `enforceReference` is `true` | `no_action` |

### Foreign key on table level
The `foreignKeys` on the table level, is a list with the following fields:

| Field            | Type    | Description                                                                                                                                                       | Default     |
|------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| tableName        | String  | Name of the table that the foreign key references                                                                                                                 |             |
| columns          | String  | List of columns that points to the foreign table <br/>- `referencingColumn`: Column in current table<br/>- `referenceColumn: Column in the table to be referenced |             |
| enforceReference | Boolean | Whether to enforce the foreign key reference                                                                                                                      |             |
| onDelete         | String  | [Action](#actions) to take when the referenced row is deleted. <br/>Only active if `enforceReference` is `true`                                                   | `no_action` |
| onUpdate         | String  | [Action](#actions) to take when the referenced row is updated  <br/>Only active if `enforceReference` is `true`                                                   | `no_action` |

### Actions
Theses are the actions available for `onDelete` and `onUpdate`:

| Action        | Description                                                                                                              |
|---------------|--------------------------------------------------------------------------------------------------------------------------|
| `cascade`     | Deletes or updates child rows automatically when the parent row is deleted or updated                                    |
| `set_null`    | Sets the foreign key column in child rows to NULL when the parent is deleted or updated                                  |       
| `set_default` | Sets the foreign key column in child rows to its default value when the parent key is deleted or updated                 |
| `restrict`    | Prevents updating the parent key or delete it, if child rows exist                                                       | 
| `no_action`   | Similar to `restrict`; the update is not allowed if child rows exist. <br/> This means no action is generated in the SQL |
 

## Examples

### gradle.build

```groovy
import dk.fust.provenance.generator.sqlscript.SqlScriptConfiguration
import dk.fust.provenance.destination.DirectoryDestination

new SqlScriptConfiguration(
        provenanceFile: new File(projectDir, 'provenance.yml'),
        destination: new DirectoryDestination(
                directory: new File('scripts'),
                createParentDirectories: true
        )
)
```

### generator-configuration.yml

```yaml
- className: dk.fust.provenance.generator.sqlscript.SqlScriptConfiguration
  provenanceFile: provenance.yml
  destination:
    className: dk.fust.provenance.destination.DirectoryDestination
    directory: scripts
    createParentDirectories: true
```

## Demo

See [provenance-demos](https://github.com/patrickfust/provenance-demos) for demo.
