package se.yolean.kafka.topic.manager;

import java.util.Collections;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import se.yolean.kafka.topic.declaration.ManagedTopic;
import se.yolean.kafka.topic.manager.schemaregistry.ManagedTopicDeserializer;

public class TopicsLogConsumer implements Runnable {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  @Inject
  @Named("bootstrap.servers")
  private String bootstrapServers;

  @Inject
  @Named("management.consumer.group.id")
  private String groupId;

  @Inject
  @Named("management.topic.name")
  private String topicName;

  @Inject
  @Named("topic.declarations.consumer.poll.timeout.ms")
  private int pollTimeout;

  @Inject
  @Named("topic.declarations.consumer.polls.max")
  private int pollsMax;

  @Inject
  private KafkaAvroDeserializer deserializerSchemaRegistry;

  @Inject
  private ManagedTopicDeserializer deserializerManagedTopic;

  private ManagedTopicHandler handler = null;

  public void setMessageHandler(ManagedTopicHandler handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Got a null handler");
    }
    if (this.handler != null) {
      throw new IllegalArgumentException("Handler already configured");
    }
    this.handler = handler;
  }

  @Override
  public void run() {
    if (handler == null) {
      throw new IllegalStateException("Can't run consumer before a message handler has been set");
    }

    Properties props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("group.id", groupId);
    // This is essential for keeping track of which declarations we've managed to process
    props.put("enable.auto.commit", "false");
    // What happens if the service is down while a management record is produced?
    // Can we get a notification when this happens? We're not sufficiently idempotent yet:
    // Or should we trust our commits completely, and use "latest", possibly requiring manual re-submit of a declaration
    props.put("auto.offset.reset", "earliest");
    // We expect this to be a low frequency topic, so we can have rather long poll times but process each record immediately
    props.put("max.poll.records", 1);

    KafkaConsumer<Object, ManagedTopic> consumer = new KafkaConsumer<>(props, deserializerSchemaRegistry, deserializerManagedTopic);
    try {
      consumer.subscribe(Collections.singleton(topicName));

      log.info("Starting consumer poll loop", "pollTimeout", pollTimeout, "pollMaxCount", pollsMax);
      for (int i = 0; pollsMax == -1 || i < pollsMax; i++) {
        ConsumerRecords<Object, ManagedTopic> records = consumer.poll(pollTimeout);
        if (records.isEmpty()) {
          log.debug("This poll consumed no declarations");
        }
        for (ConsumerRecord<Object, ManagedTopic> record : records) {
          if (handler.handle(record.value())) {
            log.info("Handler success, committing ", "partition", record.partition(), "offset", record.offset(), "managedTopicName", record.key());
            consumer.commitSync();
          } else {
            throw new RuntimeException("Failed to process topic declaration, TODO error handling for that. Exiting.");
          }
        }
      }
    } finally {
      consumer.close();
    }
  }

}
