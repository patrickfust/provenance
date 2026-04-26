package dk.fust.provenance.generator.erdiagram;

import lombok.Data;

/**
 * Filter for choosing specific tables in a group and where to replace it in the markdown
 */
@Data
public class GenerateKey {

    private String filterTags;
    private String destinationKey;

}
