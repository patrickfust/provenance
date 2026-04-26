# Generation types

The provenance generator support these provenance types, but you're free to create your own.
Just implement `dk.fust.provenance.Generator` and `dk.fust.provenance.GeneratorConfiguration`.

Add a dependency in your buildscript with the corresponding artifact id.

## Generation types supported out-of-the-box

| Artifact id                                                            | Description                        |
|------------------------------------------------------------------------|------------------------------------|
| [provenance-data-dictionary](../generators/provenance-data-dictionary) | Generates Data Dictionary          |
| [provenance-datalineage](../generators/provenance-datalineage)         | Generates Data lineage             |
| [provenance-erdiagram](../generators/provenance-erdiagram)             | Generates Entity-Relation diagrams |
| [provenance-sql-script](../generators/provenance-sql-script)           | Generates SQL-files                |
