package dk.fust.provenance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.model.Provenance;
import dk.fust.provenance.util.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to read provenance files
 */
@Slf4j
public class ProvenanceService {

    /**
     * Read the file and parse it.
     * Handles JSON and YAML
     * @param file provenance file
     * @return the parsed document
     * @throws IOException an error occurred
     */
    public Provenance loadProvenance(File file) throws IOException {
        log.debug("ProvenanceService.loadProvenance file: {}", file.getAbsolutePath());
        ObjectMapper objectMapper = createObjectMapper(file);
        return objectMapper.readValue(file, Provenance.class);
    }

    /**
     * Loads external provenance files specified in the configuration.
     * @param configuration configuration containing external provenance file paths
     * @return list of loaded Provenance objects
     * @throws IOException an error occurred
     */
    public List<Provenance> loadExternalProvenanceFiles(GeneratorConfiguration configuration) throws IOException {
        List<Provenance> files = new ArrayList<>();
        if (configuration.getExternalProvenanceFiles() != null) {
            for (String filename : configuration.getExternalProvenanceFiles()) {
                File file = new File(filename);
                Assert.isTrue(file.exists(), "External provenance file does not exist: " + file.getAbsolutePath());
                files.add(loadProvenance(file));
            }
        }
        return files;
    }

    /**
     * Reads the contents of a file into JsonNodes
     * @param file file to read
     * @return parsed Json or yaml
     * @throws IOException an error occurred
     */
    public JsonNode loadFileAsTree(File file) throws IOException {
        ObjectMapper objectMapper = createObjectMapper(file);
        return objectMapper.readTree(file);
    }

    private ObjectMapper createObjectMapper(File file) {
        JsonMapper.Builder builder;
        if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml")) {
            builder = JsonMapper.builder(new YAMLFactory());
        } else {
            builder = JsonMapper.builder();
        }
        return builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build();
    }
}
