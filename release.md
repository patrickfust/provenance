# Releasing

This documents describes how to release Provenance.

It uses [JReleaser](https://jreleaser.org/) to help package, sign and upload releases to [GitHub](https://github.com/patrickfust/provenance) and [Maven Central](https://central.sonatype.com/search?q=dk.fust).

It consists of these parts:
1. Core functionality
2. Different generator plugins
3. Different destination plugins
4. Gradle plugin
5. Maven plugin

Every part (can be released in the root folder.

## Setting version

Use the root Maven profile `set-version` to update the project version across all modules.

```zsh
./mvnw -N -Pset-version versions:set -DnewVersion=0.0.2-SNAPSHOT
```

The profile is configured in `pom.xml` with `processAllModules=true` and `generateBackupPoms=false`.

## Release all

1. Set version: `./setVersion.sh 1.0.0`
2. Build the packages plugin: `mvn clean install -U` (-U is force update from Maven central)
3. Publish it locally: `mvn -Ppublication -DskipTests`
4. Release to GitHub and Maven Central as dry-run: `./mvnw -N -Djreleaser.dry.run=true org.jreleaser:jreleaser-maven-plugin:full-release`
5. Release the real thing: `./mvnw -N org.jreleaser:jreleaser-maven-plugin:full-release`

# JRelease schema

In order to update the JRelease schema, you can run the following command:

```bash
brew upgrade jreleaser
jreleaser json-schema
```
