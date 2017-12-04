package se.yolean.kafka.topic.manager.init;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import se.yolean.kafka.topic.declaration.ManagedTopic;

/**
 * Defines the management topic itself.
 */
public class ManagementTopicDeclarationProvider implements Provider<ManagedTopic> {

  public static final String KEY_AVRO_SCHEMA = "{\"type\": \"string\"}";

  @Inject
  @Named("management.topic.name")
  private String topicName;

  @Override
  public ManagedTopic get() {
    ManagedTopic topic = new ManagedTopic();
    topic.setName(topicName);
    topic.setSchemaRegistryKeyAvro(KEY_AVRO_SCHEMA);
    topic.setSchemaRegistryValueAvro(ManagedTopic.SCHEMA$.toString());
    return topic;
  }

}
