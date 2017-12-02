package se.yolean.kafka.topic.manager.init;

import com.google.inject.AbstractModule;

import se.yolean.kafka.topic.declaration.ManagedTopic;

public class InitModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ManagedTopic.class).toProvider(ManagementTopicDeclarationProvider.class);
  }

}
