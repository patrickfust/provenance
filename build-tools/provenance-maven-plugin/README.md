# Maven plugin

You can see a demo of how to use the Provenance's Maven plugin at [provenance-demos](https://github.com/patrickfust/provenance-demos)

## Configuration

In the `build`-section of your pom, you can add the `generate` goal.

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>dk.fust.provenance</groupId>
        <artifactId>provenance-maven-plugin</artifactId>
        <version>1.0.0</version>
        <configuration>
          <provenanceConfigurationFile>generator-configuration.yml</provenanceConfigurationFile>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

Alternatively, you can have multiple configuration files:
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>dk.fust.provenance</groupId>
        <artifactId>provenance-maven-plugin</artifactId>
        <version>1.0.0</version>
        <configuration>
            <provenanceConfigurationFiles>
                <file>provenance-configuration1.yml</file>
                <file>provenance-configuration2.yml</file>
            </provenanceConfigurationFiles>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```


## Goals

There are two goals: `help` and `generate`.

### Help

Show help:

`mvn dk.fust.provenance:provenance-maven-plugin:help`

### Generate documentation

Generate the documentation:

`mvn dk.fust.provenance:provenance-maven-plugin:generate`

