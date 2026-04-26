package dk.fust.provenance.maven;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ProvenanceGeneratorMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void testLoadingMojo() throws Exception {
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        ProvenanceGeneratorMojo myMojo = rule.lookupConfiguredMojo(pom, "generate");
        assertNotNull(myMojo);
        myMojo.execute();
    }

}

