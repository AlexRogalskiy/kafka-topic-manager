package se.yolean.kafka.topic.declaration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.avro.Schema;
import org.junit.Test;

public class SchemaSourceValidation {

  public static final File SOURCE = new File("src/main/avro/ManagedTopic.avsc");

  @Test
  public void testUsingAvroLib() throws IOException {
    Schema.Parser parser = new Schema.Parser();
    Schema schema = parser.parse(SOURCE);
    assertNotNull(schema);
    System.out.println(schema.getFullName());
    System.out.println("fields: " + schema.getFields());
    System.out.println("doc: " + schema.getDoc());
    System.out.println("doc as labels: " + Arrays.asList(schema.getDoc().split(",")));
  }

}
