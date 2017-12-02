package se.yolean.kafka.topic.manager.init;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.AbstractModule;

import se.yolean.kafka.topic.manager.ManagedTopicScope;

public class ManagedTopicModule extends AbstractModule {

  private final ILogger logger = SLoggerFactory.getLogger(this.getClass());

  private ManagedTopicScope scope;

  public ManagedTopicModule(ManagedTopicScope scope) {
    this.scope = scope;
  }

  @Override
  protected void configure() {
    logger.info("Current scope set to", "topicName", scope.getDeclaration().getName());
    bind(ManagedTopicScope.class).toInstance(scope);
  }

}
