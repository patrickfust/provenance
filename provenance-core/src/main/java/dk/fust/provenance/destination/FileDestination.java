package dk.fust.provenance.destination;

import dk.fust.provenance.util.Assert;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Can write the entire document to the specified file
 */
@Data
public class FileDestination implements Destination {

    private File file;

    private boolean createParentDirectories = false;

    @Override
    public void validate() {
        Assert.isNotNull(file, "file is required");
    }

    @Override
    public void sendDocumentToDestination(String document, String destination) throws IOException {
        if (createParentDirectories && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Files.writeString(file.toPath(), document);
    }

}
