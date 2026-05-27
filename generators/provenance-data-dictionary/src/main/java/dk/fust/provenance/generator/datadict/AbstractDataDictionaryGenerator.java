package dk.fust.provenance.generator.datadict;

import dk.fust.provenance.Generator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.format.table.FormatTable;
import dk.fust.provenance.model.Provenance;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Common generator functionality for data dictionaries
 */
@Slf4j
public abstract class AbstractDataDictionaryGenerator implements Generator {

    /**
     * Generate the FormatTable for the data dictionary
     * @param provenance model of the data dictionary
     * @param configuration configuration
     * @return a table containing the relevant information
     */
    protected abstract FormatTable generateTable(Provenance provenance, AbstractDataDictionaryConfiguration configuration);

    @Override
    public void generate(Provenance provenance, GeneratorConfiguration generatorConfiguration) throws IOException {
        AbstractDataDictionaryConfiguration dataDictionaryConfiguration = (AbstractDataDictionaryConfiguration) generatorConfiguration;
        if (provenance.getDataDictionary() != null && provenance.getDataDictionary().getDataDictionaryFiles() != null) {
            sendTableToDestination(generateTable(provenance, dataDictionaryConfiguration), dataDictionaryConfiguration);
        }
    }

    private void sendTableToDestination(FormatTable formatTable, AbstractDataDictionaryConfiguration configuration) throws IOException {
        configuration.getTableFormatter().formatTableAndSendToDestination(
                formatTable, configuration.getDestination(), configuration.getKey());
    }

}
