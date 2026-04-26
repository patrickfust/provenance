package dk.fust.provenance

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import dk.fust.provenance.model.Provenance

class TestHelper {

    static File getTestFile(String filename) {
        URL url = TestHelper.classLoader.getResource(filename)
        if (url == null) {
            throw new IllegalArgumentException("url may not be null")
        }
        return new File(url.toURI())
    }

    static Provenance loadTestProvenance(String filename) {
        File file = getTestFile(filename)
        ObjectMapper objectMapper = createObjectMapper(file)
        return objectMapper.readValue(file, Provenance)
    }

    private static ObjectMapper createObjectMapper(File file) {
        JsonMapper.Builder builder
        if (file.name.endsWith('.yaml') || file.name.endsWith('.yml')) {
            builder = JsonMapper.builder(new YAMLFactory())
        } else {
            builder = JsonMapper.builder()
        }
        return builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build()
    }

}
