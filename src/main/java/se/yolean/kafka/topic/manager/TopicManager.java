package se.yolean.kafka.topic.manager;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.manager.init.ConfigModule;
import se.yolean.kafka.topic.manager.init.InitModule;
import se.yolean.kafka.topic.manager.init.MetricsModule;
import se.yolean.kafka.topic.manager.schemaregistry.SchemaRegistryModule;
import se.yolean.kafka.topic.declaration.ManagedTopic;

public class TopicManager {

  private static final ILogger log = SLoggerFactory.getLogger(TopicManager.class);

  public static void main(String[] args) {

    ConfigModule configModule = new ConfigModule(args);

    Injector appContext = Guice.createInjector(
        configModule,
        new MetricsModule(),
        new SchemaRegistryModule()
        );

    // Manager needs to be able to start its own child contexts, so we initialize it here (or can it be Guice aware?)
    ManagedTopicHandler handler = new ManagedTopicHandler(appContext);

    log.info("Initializing management configuration");
    Injector initContext = appContext.createChildInjector(new InitModule());
    ManagedTopic managementTopicDeclaration = initContext.getInstance(ManagedTopic.class);
    handler.handle(managementTopicDeclaration);

    log.info("Starting managed topics consumer");
    TopicsLogConsumer consumer = appContext.getInstance(TopicsLogConsumer.class);
    consumer.setMessageHandler(handler);
    consumer.run();
  }

}
