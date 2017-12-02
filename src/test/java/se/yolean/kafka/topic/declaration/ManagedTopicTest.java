package se.yolean.kafka.topic.declaration;

import static org.junit.Assert.*;

import org.junit.Test;

public class ManagedTopicTest {

  @Test
  public void testGeneratedClass() {
    Class<ManagedTopic> topicclass = ManagedTopic.class;
    try {
      topicclass.getDeclaredMethod("equals", Object.class);
      fail("We don't expect the generator to implement .equals");
    } catch (NoSuchMethodException e) {
      // ok
    }
  }

  @Test
  public void testSchema() {
    assertNotNull(ManagedTopic.SCHEMA$);
  }

  @Test
  public void testOperation() {
    ManagedTopic t = new ManagedTopic();
    t.setOp(Operation.UPDATE);
  }

}
