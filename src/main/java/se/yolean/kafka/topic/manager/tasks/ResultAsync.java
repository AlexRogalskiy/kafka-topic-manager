package se.yolean.kafka.topic.manager.tasks;

import java.util.concurrent.Future;

import org.apache.kafka.common.KafkaFuture;

/**
 * Indicates that a task is running but might not have completed yet.
 *
 * Several Kafka client APIs return a {@link KafkaFuture},
 * but we probably won't return individual such results.
 */
public interface ResultAsync<V> extends Result, Future<V> {

}
