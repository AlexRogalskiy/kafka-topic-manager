package se.yolean.kafka.topic.manager;

import java.util.Collections;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

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
  @Named("topic.declarations.consumer.polls.max")
  private int pollsMax;

  @Override
  public void run() {

    Properties props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("group.id", groupId);
    // this is essential for keeping track of which declarations we've managed to process
    props.put("enable.auto.commit", "false");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    // we expect this to be a low frequency topic, so simplify poll result handling
    props.put("max.poll.records", 1);

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singleton(topicName));

    for (int i = 0; pollsMax == -1 || i < pollsMax; i++) {
      log.debug("Consumer poll starting");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException("ouch", e);
      }
    }

    consumer.close();
  }

}
