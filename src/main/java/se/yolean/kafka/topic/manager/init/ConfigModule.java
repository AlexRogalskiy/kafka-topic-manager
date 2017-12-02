package se.yolean.kafka.topic.manager.init;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Properties;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigModule extends AbstractModule {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  public static final String DEFAULT_PROPERTIES_CLASSSPATH_PATH = "default.properties";

  private Properties config;

  public ConfigModule(String[] args) {
    if (args.length < 1) {
      throw new IllegalArgumentException("First argument must be the path to a config file");
    }
    File overrideProperties = new File(args[0]);

    config = new Properties();
    InputStream defaults = getClasspathProperties(DEFAULT_PROPERTIES_CLASSSPATH_PATH);
    try {
      config.load(defaults);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read built in defaults from properties " + DEFAULT_PROPERTIES_CLASSSPATH_PATH, e);
    }
    Reader overrides;
    try {
      overrides = getPathProperties(overrideProperties);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("Properties with path from args not found: " + overrideProperties.getPath());
    }
    try {
      config.load(overrides);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read properties: " + overrideProperties.getAbsolutePath());
    }
  }

  protected Properties getConfig() {
    return this.config;
  }

  @Override
  protected void configure() {
    Properties properties = getConfig();
    logConfigValues(properties, log);
    Names.bindProperties(super.binder(), properties);
  }

  protected InputStream getClasspathProperties(String propertiesClassspathPath) throws IllegalArgumentException {
    InputStream resource = this.getClass().getResourceAsStream(propertiesClassspathPath);
    if (resource == null) {
      throw new IllegalArgumentException("Failed to read classpath resource " + propertiesClassspathPath);
    }
    return resource;
  }

  protected Reader getPathProperties(File overrideProperties) throws FileNotFoundException {
    return new FileReader(overrideProperties);
  }

  // I'd like to print runtime config like Kafka clients do, but can't find the utility
  static void logConfigValues(Properties properties, ILogger log) {
    StringWriter writer = new StringWriter();
    properties.list(new PrintWriter(writer));
    log.info("Instance config: " + writer.getBuffer().toString());
  }

}
