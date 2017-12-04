package se.yolean.kafka.topic.manager.schemaregistry;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class KafkaAvroSerializerProvider implements Provider<KafkaAvroSerializer> {

  @Inject
  private SchemaRegistryClient schemaRegistryClient;

  // apparently we have to provide the url again, despite providing the client
  @Inject
  @Named("schema.registry.url")
  public String schemaRegistryUrl;

  @Override
  public KafkaAvroSerializer get() {
    Map<String, Object> props = new HashMap<>(1);
    props.put("schema.registry.url", schemaRegistryUrl);
    KafkaAvroSerializer serializer = new KafkaAvroSerializer(schemaRegistryClient, props);
    return serializer;
  }

}
