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

  /**
   * @return access to the generated class' instance, don't mutate!
   */
  public ManagedTopic getDeclaration() {
    return declaration;
  }

}
