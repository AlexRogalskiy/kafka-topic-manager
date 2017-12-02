package se.yolean.kafka.topic.manager.schemaregistry;

import com.google.inject.AbstractModule;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import se.yolean.kafka.topic.manager.tasks.SchemaUpdate;

public class SchemaRegistryModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(org.apache.avro.Schema.Parser.class);
    bind(SchemaRegistryClient.class).toProvider(SchemaRegistryClientProvider.class);
    bind(SchemaUpdate.class).to(SchemaRegistryUpdate.class);
  }

}
