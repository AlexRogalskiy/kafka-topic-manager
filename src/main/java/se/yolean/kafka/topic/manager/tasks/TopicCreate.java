package se.yolean.kafka.topic.manager.tasks;

import se.yolean.kafka.topic.manager.kafkaadmin.TopicResult;
import se.yolean.kafka.topic.declaration.ManagedTopic;
import se.yolean.kafka.topic.declaration.Operation;

/**
 * We're far from an Update operation until we've figured out how to handle
 * {@link Operation#UPDATE} compared to {@link Operation#EXIST},
 * given that both broker config and this service's config can change at any time.
 *
 * For example can we support edited replication settiongs?
 * How do we define replication in {@link ManagedTopic},
 * given that we may have used current default.replication.factor
 * in a previous {@link Operation#EXIST}.
 */
public interface TopicCreate extends Task<TopicResult> {

}
