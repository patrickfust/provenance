package dk.fust.provenance.gradle;

import dk.fust.provenance.ProvenanceGenerator;
import dk.fust.provenance.GeneratorConfiguration;
import dk.fust.provenance.service.ProvenanceConfigurationLoaderService;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Generates provenance artifacts based upon a single-source-of-truth yaml file.
 */
public class ProvenanceGeneratorPlugin implements Plugin<Project> {
    private static final Logger log = LoggerFactory.getLogger(ProvenanceGeneratorPlugin.class);

    @Override
    public void apply(Project project) {
        String projectDir = project.getProjectDir().getAbsolutePath();
        ProvenanceGeneratorPluginExtension extension = project.getExtensions().create("provenanceGenerator", ProvenanceGeneratorPluginExtension.class);

        project.getTasks().register("generateProvenance", task -> {
            task.setDescription("Generates provenance artifacts");
            task.setGroup("provenance");
            task.doLast(t -> {
                log.debug("Validating configuration...");
                if (extension.getGeneratorConfigurations() == null && extension.getProvenanceConfigurationFile() == null) {
                    throw new IllegalArgumentException("Need either GeneratorConfigurations or ProvenanceConfigurationFile");
                }
                if (extension.getGeneratorConfigurations() != null && !extension.getGeneratorConfigurations().isEmpty() &&
                        extension.getProvenanceConfigurationFile() != null) {
                    throw new IllegalArgumentException("Can't have both GeneratorConfigurations and ProvenanceConfigurationFile");
                }

                ProvenanceGenerator provenanceGenerator = new ProvenanceGenerator();
                if (extension.getGeneratorConfigurations() != null && !extension.getGeneratorConfigurations().isEmpty()) {
                    provenanceGenerator.generate(extension.getGeneratorConfigurations());
                } else {
                    ProvenanceConfigurationLoaderService provenanceConfigurationLoaderService = new ProvenanceConfigurationLoaderService();
                    try {
                        List<GeneratorConfiguration> confs;
                        File provenanceConfigurationFile = extension.getProvenanceConfigurationFile();
                        if (provenanceConfigurationFile.exists()) {
                            log.debug("Found configuration file: {}", provenanceConfigurationFile.getPath());
                            confs = provenanceConfigurationLoaderService.readConfigurations(provenanceConfigurationFile);
                        } else {
                            File projectConfigurationFile = new File(projectDir, provenanceConfigurationFile.getPath());
                            log.debug("Could not find configuration file -> trying i project dir: " + projectConfigurationFile.getAbsolutePath());
                            log.debug("Exists now: " + projectConfigurationFile.exists());
                            if (projectConfigurationFile.exists()) {
                                log.debug("Found project configuration file: {}", projectConfigurationFile.getPath());
                            }
                            provenanceGenerator.setBaseDir(new File(projectDir));
                            confs = provenanceConfigurationLoaderService.readConfigurations(projectConfigurationFile);
                        }
                        log.debug("Calling generate...");
                        provenanceGenerator.generate(confs);
                    } catch (IOException e) {
                        throw new RuntimeException("Can't load generator configurations", e);
                    }
                }
            });
        });
    }
}
