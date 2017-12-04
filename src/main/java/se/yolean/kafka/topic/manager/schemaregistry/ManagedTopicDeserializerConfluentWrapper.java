package se.yolean.kafka.topic.manager.schemaregistry;

import java.util.Map;

import javax.inject.Inject;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import se.yolean.kafka.topic.declaration.ManagedTopic;

public class ManagedTopicDeserializerConfluentWrapper implements ManagedTopicDeserializer {

  private final ILogger logger = SLoggerFactory.getLogger(this.getClass());

  @Inject
  private KafkaAvroDeserializer schemaRegistryDeserializer;

  @Override
  public ManagedTopic deserialize(String topic, byte[] data) {
    logger.debug("Deserializing", "topic", topic, "data", new String(data));
    ManagedTopic record = new ManagedTopic();
    Object deserialize = schemaRegistryDeserializer.deserialize(topic, data);
    deserialize.getClass();
    return record;
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    schemaRegistryDeserializer.configure(configs, isKey);
  }

  @Override
  public void close() {
    schemaRegistryDeserializer.close();
  }

}
