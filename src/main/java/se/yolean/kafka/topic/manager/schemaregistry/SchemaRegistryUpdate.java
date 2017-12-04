package se.yolean.kafka.topic.manager.schemaregistry;

import java.io.IOException;

import javax.inject.Inject;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import se.yolean.kafka.topic.manager.ManagedTopicScope;
import se.yolean.kafka.topic.manager.TODOErrorHandling;
import se.yolean.kafka.topic.manager.tasks.SchemaUpdate;

public class SchemaRegistryUpdate implements SchemaUpdate {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  // https://docs.confluent.io/current/schema-registry/docs/serializer-formatter.html#serializer
  public static final String TOPIC_SCHEMA_VALUE_SUFFIX = "-value";
  public static final String TOPIC_SCHEMA_KEY_SUFFIX = "-key";

  @Inject
  private SchemaRegistryClient client;

  @Inject
  private org.apache.avro.Schema.Parser parser;

  @Inject
  private ManagedTopicScope managed;

  @Override
  public SchemaResult call() throws Exception {
    SchemaResult result = new SchemaResult();

    String keySchemaSource = managed.getSchemaRegistryKeyAvro();
    if (keySchemaSource != null) {
      int keySchemaId = update(keySchemaSource, TOPIC_SCHEMA_KEY_SUFFIX);
      log.info("Key schema registered for", "declaration", managed.getName(), "id", keySchemaId);
      result.setKeySchemaId(keySchemaId);
    } else {
      log.debug("Declaration contains no key schema", "declaration", managed.getName());
    }

    String valueSchemaSource = managed.getSchemaRegistryValueAvro();
    if (valueSchemaSource != null) {
      int valueSchemaId = update(valueSchemaSource, TOPIC_SCHEMA_VALUE_SUFFIX);
      log.info("Value schema registered for", "declaration", managed.getName(), "id", valueSchemaId);
      result.setValueSchemaId(valueSchemaId);
    } else {
      log.debug("Declaration contains no value schema", "declaration", managed.getName());
    }

    return result;
  }

  private int update(String avroSource, String topicSchemaSuffix) {
    if (avroSource == null) {
      log.info("No schema update needed. Declaration contains no schema");
    }

    String subject = managed.getName() + topicSchemaSuffix;
    org.apache.avro.Schema avro = parser.parse(avroSource);

    int resultSchemaId;
    try {
      resultSchemaId = client.register(subject, avro);
    } catch (IOException e) {
      throw new TODOErrorHandling(e);
    } catch (RestClientException e) {
      throw new TODOErrorHandling(e);
    }
    return resultSchemaId;
  }

}
