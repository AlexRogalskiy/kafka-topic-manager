package se.yolean.kafka.topic.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.name.Names;

import se.yolean.kafka.topic.manager.config.ConfigModule;

public class ItestConfigModule extends ConfigModule {

  private Properties overrides = new Properties();

  private static File getItestPropertiesForCurrentTestSetup() {
    // Currently the only test setup we support is my local docker-compose.yml
    // But we can easily add more flexibility, by env vars for example, later
    return new File("src/itest/resources/itest-dockercompose.properties");
  }

  public ItestConfigModule() {
    super(new String[] { getItestPropertiesForCurrentTestSetup().getPath() });
  }

  public ItestConfigModule override(String key, String value) {
    overrides.put(key, value);
    return this;
  }

  public ItestConfigModule override(String key, int value) {
    overrides.put(key, Integer.toString(value));
    return this;
  }

  @Override
  protected InputStream getClasspathProperties(String propertiesClassspathPath) {
    File srcMainResourcesFile = new File("src/main/resources/" + propertiesClassspathPath);
    try {
      return new FileInputStream(srcMainResourcesFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to find " + propertiesClassspathPath + " as test resource " + srcMainResourcesFile.getAbsolutePath());
    }
  }

  @Override
  protected Properties getConfig() {
    Properties overridden = new Properties(super.getConfig());
    overridden.putAll(overrides);
    return overridden;
  }

}
