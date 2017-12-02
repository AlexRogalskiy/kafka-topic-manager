package se.yolean.kafka.topic.manager;

import java.util.LinkedList;
import java.util.List;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.Injector;

import se.yolean.kafka.topic.manager.init.ManagedTopicModule;
import se.yolean.kafka.topic.manager.tasks.Result;
import se.yolean.kafka.topic.manager.tasks.SchemaUpdate;
import se.yolean.kafka.topic.manager.tasks.Task;
import se.yolean.kafka.topic.manager.tasks.TasksModule;
import se.yolean.kafka.topic.manager.tasks.TopicCreate;
import se.yolean.kafka.topic.declaration.ManagedTopic;

/**
 * Invoked when a new topic declaration is found.
 */
public class ManagedTopicHandler {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private Injector appContext;

  private TasksModule tasksModule;

  public ManagedTopicHandler(Injector appContext, TasksModule tasksModule) {
    this.appContext = appContext;
    this.tasksModule = tasksModule;
  }

  /**
   * @return true if/when caller should commit (the declaration message) as processed.
   */
  public boolean handle(ManagedTopic declaration) {
    log.info("Handle", "topic", declaration.getName());

    ManagedTopicScope scope = new ManagedTopicScope(declaration);

    Injector handlerContext = appContext.createChildInjector(
        new ManagedTopicModule(scope),
        tasksModule);

    // Should probably based on analysis of the ManagedTopic, maybe as rules in ManagedTopicScope
    List<Class<? extends Task<?>>> tasks = new LinkedList<>();
    tasks.add(TopicCreate.class);
    tasks.add(SchemaUpdate.class);

    log.info("Tasks", "list", tasks);

    for (Class<? extends Task<?>> taskClass : tasks) {
      Task<?> task = handlerContext.getInstance(taskClass);
      Result result;
      try {
        result = task.call();
      } catch (Exception e) {
        throw new TODOErrorHandling(e);
      }
      if (result == null) {
        throw new IllegalStateException("Missing result object from task " + taskClass);
      }
    }

    log.warn("Handler completed, but TODO check for async Results");
    return true;
  }

}
