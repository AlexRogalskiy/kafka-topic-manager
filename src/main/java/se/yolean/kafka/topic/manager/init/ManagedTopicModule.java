package se.yolean.kafka.topic.manager.init;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.AbstractModule;

import se.yolean.kafka.topic.manager.ManagedTopicScope;

public class ManagedTopicModule extends AbstractModule {

  private final ILogger logger = SLoggerFactory.getLogger(this.getClass());

  private ManagedTopicScope managed;

  public ManagedTopicModule(ManagedTopicScope scope) {
    this.managed = scope;
  }

  @Override
  protected void configure() {
    logger.info("Current scope set to", "topicName", managed.getName());
    bind(ManagedTopicScope.class).toInstance(managed);
  }

}
