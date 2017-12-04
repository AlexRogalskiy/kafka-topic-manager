package se.yolean.kafka.topic.manager.schemaregistry;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class KafkaAvroDeserializerProvider implements Provider<KafkaAvroDeserializer> {

  @Inject
  private SchemaRegistryClient schemaRegistryClient;

  // apparently we have to provide the url again, despite providing the client
  @Inject
  @Named("schema.registry.url")
  public String schemaRegistryUrl;

  @Override
  public KafkaAvroDeserializer get() {
    Map<String, Object> props = new HashMap<>(1);
    props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    // but how do we tell it to deserialize to ManagedTopic?
    // - trying a wrapper class now
    //props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
    KafkaAvroDeserializer deserializer = new KafkaAvroDeserializer(schemaRegistryClient, props);
    return deserializer;
  }

}
