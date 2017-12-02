package se.yolean.kafka.topic.manager.tasks;

import com.google.inject.AbstractModule;

public abstract class ConfigWithTasks extends AbstractModule {

  public abstract AbstractModule withTasks(TasksModule tasksModule);

}
