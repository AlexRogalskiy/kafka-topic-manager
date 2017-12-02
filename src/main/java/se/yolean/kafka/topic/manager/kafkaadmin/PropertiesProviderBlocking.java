package se.yolean.kafka.topic.manager.kafkaadmin;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

/**
 * {@link AdminClient#create(Properties)} does i/o so we can only inject the Properties for it.
 */
public class PropertiesProviderBlocking implements Provider<Properties> {

  @Inject
  @Named("bootstrap.servers")
  private String bootstrap;

  @Override
  public Properties get() {
    Properties props = new Properties();
    props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    return props;
  }

}
