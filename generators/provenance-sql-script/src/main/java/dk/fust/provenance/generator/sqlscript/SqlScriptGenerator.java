package dk.fust.provenance.generator.sqlscript;

import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.generator.sqlscript.generators.SqlGenerator;
import dk.fust.provenance.generator.sqlscript.generators.SqlGeneratorFactory;
import dk.fust.provenance.util.Assert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Generator for SQL files
 */
@Slf4j
@Data
public class SqlScriptGenerator implements Generator {

    @Override
    public void generate(Provenance provenance, GeneratorConfiguration generatorConfiguration) throws IOException {
        log.debug("Generating SQL-scripts...");
        Assert.isTrue(generatorConfiguration instanceof SqlScriptConfiguration, "configuration must be an instance of SqlScriptConfiguration");
        SqlScriptConfiguration sqlScriptConfiguration = (SqlScriptConfiguration) generatorConfiguration;
        SqlGenerator sqlGenerator = SqlGeneratorFactory.getSqlGenerator(sqlScriptConfiguration.getSqlDialect());
        sqlGenerator.generate(provenance, sqlScriptConfiguration);
    }

}
