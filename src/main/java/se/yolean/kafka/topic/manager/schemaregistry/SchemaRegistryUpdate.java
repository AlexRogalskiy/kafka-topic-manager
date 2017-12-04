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

  // Essential Schema Registry convention
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

    if (managed.getSchemaRegistryValueAvro() == null) {
      log.info("No schema update needed. Declaration contains no schema");
    }

    String valueSubject = managed.getName() + TOPIC_SCHEMA_VALUE_SUFFIX;
    org.apache.avro.Schema valueAvro = parser.parse(managed.getSchemaRegistryValueAvro());

    int valueSchemaId;
    try {
      valueSchemaId = client.register(valueSubject, valueAvro);
    } catch (IOException e) {
      throw new TODOErrorHandling(e);
    } catch (RestClientException e) {
      throw new TODOErrorHandling(e);
    }

    // TODO do we get a new version every time now?
    log.info("Schema registered for value", "id", valueSchemaId);

    return new SchemaResult().setValueSchemaId(valueSchemaId);
  }

}
