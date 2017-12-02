package se.yolean.kafka.topic.manager;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.manager.init.ManagementTopicDeclarationProvider;
import se.yolean.kafka.topic.manager.tasks.TasksModule;

public class ManagedTopicHandlerIntegrationTest {

  @Test
  public void testTopicManagerStartupFlow() {
    final String ID = "test-" + this.getClass().getSimpleName() + "-" + System.currentTimeMillis();
    final String IDT = ID + "-mgmt";

    TasksModule tasksModule = new TasksModule();
    Injector context = Guice.createInjector(new ItestConfigModule()
        .override("management.topic.name", IDT)
        .override("management.consumer.group.id", IDT)
        .override("management.topic.rest.producer.name", IDT)
        .override("topic.declarations.consumer.polls.max", 2)
        .override("topic.declarations.consumer.poll.timeout.ms", 1001),
        new se.yolean.kafka.topic.manager.kafkaadmin.Config().withTasks(tasksModule),
        new se.yolean.kafka.topic.manager.schemaregistry.Config().withTasks(tasksModule)
        );

    // Configure the management topic
    ManagedTopicHandler handler = new ManagedTopicHandler(context, tasksModule);
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
