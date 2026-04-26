# Gradle plugin

## Dependency

### gradle.properties
Add the version of Provenance you want to use in `gradle.properties`.
```properties
provenanceVersion = 1.0.0
```

### build.gradle
Now you can add a dependency to the gradle-plugin.
```groovy
import dk.fust.provenance.gradle.ProvenanceGeneratorPlugin

buildscript {
    dependencies {
        classpath("dk.fust.provenance:provenance-gradle-plugin:${provenanceVersion}")
    }
}

apply plugin: ProvenanceGeneratorPlugin
```

For every kind of documentation or artifact you want generated, you'll need to add the corresponding dependency in the build script-section.

For example:
```groovy
classpath("dk.fust.provenance:provenance-generator-sql-script:${provenanceVersion}")
```

## Gradle task

### Configuration in build.gradle

```groovy
import dk.fust.provenance.generator.XXX
import dk.fust.provenance.destination.XXX

provenanceGenerator {
    generatorConfigurations = [
            ... list of configurations ...
    ]
}
```

### Configuration in separate file

Another option is to have your configuration in a separate file.
[See here the description of the configuration file](../README.md#generator-configuration-file)

The list of configurations consists of the desired configurations.

```groovy
provenanceGenerator {
    provenanceConfigurationFile = new File('generator-configuration.yml')
}
```

## Generate documentation 

To generate documentation, run:
```shell
gradle generateDocumentation
```
