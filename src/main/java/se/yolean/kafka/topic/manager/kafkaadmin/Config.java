package se.yolean.kafka.topic.manager.kafkaadmin;

import java.util.Properties;

import com.google.inject.AbstractModule;

import se.yolean.kafka.topic.manager.tasks.ConfigWithTasks;
import se.yolean.kafka.topic.manager.tasks.TasksModule;
import se.yolean.kafka.topic.manager.tasks.TopicCreate;

public class Config extends ConfigWithTasks {

  @Override
  protected void configure() {
    bind(Properties.class).annotatedWith(KafkaAdminBlocking.class).toProvider(PropertiesProviderBlocking.class);
  }

  @Override
  public AbstractModule withTasks(TasksModule tasksModule) {
    tasksModule.add(TopicCreate.class, TopicCreateAssumeSuccess.class);
    return this;
  }

}
