package se.yolean.kafka.topic.manager.kafkaadmin;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Configuration for admin operations that include retries and return a completed result, not a future.
 *
 * I.e. long timeouts.
 *
 * Also ensures that we don't collide with other bound Properties.
 */
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface KafkaAdminBlocking {}
