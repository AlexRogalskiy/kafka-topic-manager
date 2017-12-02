package se.yolean.kafka.topic.manager.schemaregistry;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import se.yolean.kafka.topic.manager.tasks.SchemaUpdate;
import se.yolean.kafka.topic.manager.tasks.TasksModule;

public class SchemaRegistryModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(org.apache.avro.Schema.Parser.class);
    bind(SchemaRegistryClient.class).toProvider(SchemaRegistryClientProvider.class);
  }

  public Module withTasks(TasksModule tasksModule) {
    tasksModule.add(SchemaUpdate.class, SchemaRegistryUpdate.class);
    return this;
  }

}
