package se.yolean.kafka.topic.manager.schemaregistry;

import java.util.Map;

import javax.inject.Inject;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import se.yolean.kafka.topic.declaration.ManagedTopic;

public class ManagedTopicDeserializerSpecific implements ManagedTopicDeserializer {

  private final ILogger logger = SLoggerFactory.getLogger(this.getClass());

  @Inject
  private KafkaAvroDeserializer schemaRegistryDeserializer;

  @Override
  public ManagedTopic deserialize(String topic, byte[] data) {
    logger.debug("Deserializing", "topic", topic, "data", new String(data));
    Object specific = schemaRegistryDeserializer.deserialize(topic, data);
    return convert(specific);
  }

  protected ManagedTopic convert(Object deserializedByKafkaAvro) {
    return (ManagedTopic) deserializedByKafkaAvro;
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Probably not called, because we set an instance on KafkaProducer
    logger.debug("Config", KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, configs.get(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG));
    schemaRegistryDeserializer.configure(configs, isKey);
  }

  @Override
  public void close() {
    schemaRegistryDeserializer.close();
  }

}
