package se.yolean.kafka.topic.manager;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TopicsLogConsumerIntegrationTest {

  @Test
  public void test() {
    Injector context = Guice.createInjector(new ItestConfigModule()
        .override("topic.declarations.consumer.polls.max", 3));

    TopicsLogConsumer consumer = context.getInstance(TopicsLogConsumer.class);
    consumer.run();
  }

}
