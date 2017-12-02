package se.yolean.kafka.topic.manager.schemaregistry;

import se.yolean.kafka.topic.manager.tasks.ResultCompletedAtReturn;

public class SchemaResult implements ResultCompletedAtReturn {

  private int keySchemaId;
  private int valueSchemaId;

  SchemaResult() {
  }

  public int getKeySchemaId() {
    return keySchemaId;
  }

  SchemaResult setKeySchemaId(int keySchemaId) {
    this.keySchemaId = keySchemaId;
    return this;
  }

  public int getValueSchemaId() {
    return valueSchemaId;
  }

  SchemaResult setValueSchemaId(int valueSchemaId) {
    this.valueSchemaId = valueSchemaId;
    return this;
  }

}
