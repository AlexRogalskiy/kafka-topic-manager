package se.yolean.kafka.topic.manager.tasks;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.AbstractModule;

import se.yolean.kafka.topic.manager.ManagedTopicScope;

public class TasksModule extends AbstractModule {

  private final ILogger logger = SLoggerFactory.getLogger(this.getClass());

  private Map<Class<?>, Class<?>> added = new LinkedHashMap<>(0);

  /**
   * Can't bind tasks when we bind the integration modules because the depend on {@link ManagedTopicScope}.
   * @param task bind(task)
   * @param impl .to(impl)
   */
  public <T> void add(Class<T> task, Class<? extends T> impl) {
    logger.debug("Adding task", "type", task, "impl", impl);
    if (added.containsKey(task)) {
      throw new IllegalStateException("Tasks already contains key " + task);
    }
    added.put(task, impl);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" }) // The add method should ensure class compatibility
  @Override
  protected void configure() {
    for (Class<?> key : added.keySet()) {
      bind((Class) key).to((Class) added.get(key));
    }
  }

}
