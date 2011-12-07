package com.yammer.dropwizard.config.tests;

import com.google.common.io.Resources;
import com.yammer.dropwizard.config.ConfigurationException;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;
import org.junit.Test;
import org.yaml.snakeyaml.error.YAMLException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.File;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

public class ConfigurationFactoryTest {
    @SuppressWarnings("UnusedDeclaration")
    public static class Example {
        @NotNull
        @Pattern(regexp = "[\\w]+[\\s]+[\\w]+")
        private String name;

        public String getName() {
            return name;
        }
    }

    private final Validator validator = new Validator();
    private final ConfigurationFactory<Example> factory = ConfigurationFactory.forClass(Example.class, validator);
    private final File malformedFile = new File(Resources.getResource("factory-test-malformed.yml").getFile());
    private final File invalidFile = new File(Resources.getResource("factory-test-invalid.yml").getFile());
    private final File validFile = new File(Resources.getResource("factory-test-valid.yml").getFile());

    @Test
    public void loadsValidConfigFiles() throws Exception {
        final Example example = factory.build(validFile);
        assertThat(example.getName(),
                   is("Coda Hale"));
    }

    @Test
    public void throwsAnExceptionOnMalformedFiles() throws Exception {
        try {
            factory.build(malformedFile);
            fail("expected a YAMLException to be thrown, but none was");
        } catch (YAMLException e) {
            assertThat(e.getMessage(), startsWith("null"));
        }
    }

    @Test
    public void throwsAnExceptionOnInvalidFiles() throws Exception {
        try {
            factory.build(invalidFile);
        } catch (ConfigurationException e) {
            assertThat(e.getMessage(),
                       endsWith("factory-test-invalid.yml has the following errors:\n" +
                                "  * name must match \"[\\w]+[\\s]+[\\w]+\" (was Boop)"));
        }
    }
}