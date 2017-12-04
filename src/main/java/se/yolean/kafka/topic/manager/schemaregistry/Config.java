package se.yolean.kafka.topic.manager.schemaregistry;

import com.google.inject.AbstractModule;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import se.yolean.kafka.topic.manager.tasks.ConfigWithTasks;
import se.yolean.kafka.topic.manager.tasks.SchemaUpdate;
import se.yolean.kafka.topic.manager.tasks.TasksModule;

public class Config extends ConfigWithTasks {

  @Override
  protected void configure() {
    bind(org.apache.avro.Schema.Parser.class);
    bind(SchemaRegistryClient.class).toProvider(SchemaRegistryClientProvider.class);
    bind(KafkaAvroDeserializer.class).toProvider(KafkaAvroDeserializerProvider.class);
    bind(KafkaAvroSerializer.class).toProvider(KafkaAvroSerializerProvider.class);
    bind(ManagedTopicDeserializer.class).to(ManagedTopicDeserializerConfluentWrapper.class);
  }

  @Override
  public AbstractModule withTasks(TasksModule tasksModule) {
    tasksModule.add(SchemaUpdate.class, SchemaRegistryUpdate.class);
    return this;
  }

}
