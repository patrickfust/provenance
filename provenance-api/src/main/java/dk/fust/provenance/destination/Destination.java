package dk.fust.provenance.destination;

import java.io.IOException;

/**
 * This interface describes a way to send the generated
 * artifact to the destination.
 * It could be a file, markdown, confluence or something completely different.
 */
public interface Destination {

    /**
     * Is everything all-right?
     */
    void validate();

    /**
     * Send the generated artifact to the destination
     * @param artifact artifact to send
     * @param destination where the artifact should be sent to
     * @throws IOException error occurred
     */
    void sendDocumentToDestination(String artifact, String destination) throws IOException;

}
