package se.yolean.kafka.topic.manager;

import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.manager.config.ConfigModule;
import se.yolean.kafka.topic.manager.config.MetricsModule;
import se.yolean.kafka.topic.declaration.ManagedTopic;

public class TopicManager {

  /**
   * We trust consumer commits to keep track of our position in the
   * {@link ManagedTopic} log, across service restarts and replications.
   */
  public static final String CONSUMER_GROUP_ID = "";

  public static void main(String[] args) {

    ConfigModule configModule = new ConfigModule(args);

    Injector initContext = Guice.createInjector(configModule, new MetricsModule());

    Injector consumerContext = initContext.createChildInjector();

    consumerContext.getInstance(TopicsLogConsumer.class);
  }

}
