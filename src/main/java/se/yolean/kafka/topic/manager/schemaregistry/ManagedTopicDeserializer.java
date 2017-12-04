package se.yolean.kafka.topic.manager.schemaregistry;

import org.apache.kafka.common.serialization.Deserializer;

import se.yolean.kafka.topic.declaration.ManagedTopic;

/**
 * To get the proper record type from consumer poll.
 */
public interface ManagedTopicDeserializer extends Deserializer<ManagedTopic> {
}
