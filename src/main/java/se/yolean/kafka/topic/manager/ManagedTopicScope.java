package se.yolean.kafka.topic.manager;

import se.yolean.kafka.topic.declaration.ManagedTopic;

/**
 * Most of this application is written to run per topic declaration.
 */
public class ManagedTopicScope {

  private final ManagedTopic declaration;

  public ManagedTopicScope(ManagedTopic topicDeclaration) {
    this.declaration = topicDeclaration;
  }

  public String getName() {
    return this.declaration.getName();
  }

  public String getSchemaRegistryKeyAvro() {
    return this.declaration.getSchemaRegistryKeyAvro();
  }

  public String getSchemaRegistryValueAvro() {
    return this.declaration.getSchemaRegistryValueAvro();
  }

}
