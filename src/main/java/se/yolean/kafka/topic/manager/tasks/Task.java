package se.yolean.kafka.topic.manager.tasks;

import java.util.concurrent.Callable;

public interface Task<T extends Result> extends Callable<T> {

}
