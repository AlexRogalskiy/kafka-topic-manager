package se.yolean.kafka.topic.manager;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.manager.init.ManagementTopicDeclarationProvider;
import se.yolean.kafka.topic.manager.schemaregistry.SchemaRegistryModule;

public class ManagedTopicHandlerIntegrationTest {

  @Test
  public void testTopicManagerStartupFlow() {
    final String ID = "test-" + this.getClass().getSimpleName() + "-" + System.currentTimeMillis();
    final String IDT = ID + "-mgmt";

    Injector context = Guice.createInjector(new ItestConfigModule()
        .override("management.topic.name", IDT)
        .override("management.consumer.group.id", IDT)
        .override("management.topic.rest.producer.name", IDT)
        .override("topic.declarations.consumer.polls.max", 3),
        new SchemaRegistryModule()
        );

    // Configure the management topic
    ManagedTopicHandler handler = new ManagedTopicHandler(context);
    ManagementTopicDeclarationProvider mgmtProvider = context.getInstance(ManagementTopicDeclarationProvider.class);

    // Run init
    boolean result = handler.handle(mgmtProvider.get());
    assertTrue("Handler should return success", result);

    // Test that we can consume the configured management topic
    TopicsLogConsumer consumer = context.getInstance(TopicsLogConsumer.class);
    ManagedTopicHandler mockHandler = Mockito.mock(ManagedTopicHandler.class);
    consumer.setMessageHandler(mockHandler);
    consumer.run();
  }

}
