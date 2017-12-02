package se.yolean.kafka.topic.manager;

public class TODOErrorHandling extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TODOErrorHandling(Throwable actual) {
    super("TODO implement error handling for:", actual);
  }

}
