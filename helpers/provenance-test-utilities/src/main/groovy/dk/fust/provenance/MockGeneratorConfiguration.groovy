package dk.fust.provenance

import dk.fust.provenance.destination.Destination
import dk.fust.provenance.model.annotation.MergeWithDefault

class MockGeneratorConfiguration implements GeneratorConfiguration {

    File provenanceFile
    Destination destination
    int anInt
    @MergeWithDefault Integer aBigInteger = 100
    boolean aBoolean
    @MergeWithDefault Boolean aBigBoolean = Boolean.TRUE
    long aLong
    Long aBigLong
    @MergeWithDefault String aString = "A String"
    MockEnum mockEnum

    @MergeWithDefault MockGeneratorConfigurationNested nestedConfiguration = new MockGeneratorConfigurationNested(true, "Nested String")

    @Override
    void validate() {
    }

    @Override
    Generator getGenerator() {
        return null;
    }

}
