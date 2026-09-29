package dk.fust.provenance.model;

/**
 * Data types
 */
public enum DataType {

    /**
     * Integer
     */
    INT,

    /**
     * Array of integers
     */
    INT_ARRAY,

    /**
     * Date
     */
    DATE,

    /**
     * Array of dates
     */
    DATE_ARRAY,

    /**
     * Timestamp
     */
    TIMESTAMPTZ,

    /**
     * Array of timestamps
     */
    TIMESTAMPTZ_ARRAY,

    /**
     * Numeric
     */
    NUMERIC,

    /**
     * Array of numerics
     */
    NUMERIC_ARRAY,

    /**
     * Boolean
     */
    BOOL,

    /**
     * Array of booleans
     */
    BOOL_ARRAY,

    /**
     * Big integer
     */
    BIGINT,

    /**
     * Array of big integers
     */
    BIGINT_ARRAY,

    /**
     * Text (String)
     */
    TEXT,

    /**
     * Array of text (String)
     */
    TEXT_ARRAY,

    /**
     * UUID
     */
    UUID,

    /**
     * Array of UUIDs
     */
    UUID_ARRAY,

    /**
     * JSON
     */
    JSON,

    /**
     * Array of JSON
     */
    JSON_ARRAY,

    /**
     * JSONB
     */
    JSONB,

    /**
     * Array of JSONB
     */
    JSONB_ARRAY,

    /**
     * JSONPATH
     */
    JSONPATH,

    /**
     * Array of JSONPATH
     */
    JSONPATH_ARRAY
    ;

    /**
     * We want it to be in lower case when documenting
     * @return the enum in lower case
     */
    public String toLowerCase() {
        return name().toLowerCase().replace("_array", "[]");
    }

    /**
     * Make sure that `_array` is replaced with `[]` and the rest is upper case
     * @return the enum in upper case
     */
    public String toUpperCase() {
        return toLowerCase().toUpperCase();
    }

}
