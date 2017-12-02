package se.yolean.kafka.topic.manager.kafkaadmin;

import java.util.Collections;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import se.yolean.kafka.topic.manager.ManagedTopicScope;
import se.yolean.kafka.topic.manager.tasks.TopicCreate;

public class TopicCreateAssumeSuccess implements TopicCreate {

  private final ILogger logger = SLoggerFactory.getLogger(this.getClass());

  @Inject
  @KafkaAdminBlocking
  private Properties adminClientConfig;

  @Inject
  private ManagedTopicScope managed;

  @Override
  public TopicResult call() throws Exception {

    AdminClient client = AdminClient.create(adminClientConfig);

    int numPartitions = 1;
    short replicationFactor = 1;
    NewTopic newTopic = new NewTopic(managed.getName(), numPartitions, replicationFactor);
    CreateTopicsResult createTopics = client.createTopics(Collections.singleton(newTopic));
    KafkaFuture<Void> all = createTopics.all();
    all.get();
    logger.info("Topic created, probably");

    return new TopicResult();
  }

}
