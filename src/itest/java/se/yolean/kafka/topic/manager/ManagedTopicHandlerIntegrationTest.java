package se.yolean.kafka.topic.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import se.yolean.kafka.topic.declaration.ManagedTopic;
import se.yolean.kafka.topic.manager.init.ManagementTopicDeclarationProvider;
import se.yolean.kafka.topic.manager.tasks.TasksModule;

public class ManagedTopicHandlerIntegrationTest {

  @Test
  public void testTopicManagerStartupFlow() throws InterruptedException, ExecutionException, TimeoutException {
    // test name and test topic, to be able to re-run tests in existing setup
    final String TN = "test-" + this.getClass().getSimpleName() + "-" + System.currentTimeMillis();
    final String TT = TN + "-mgmt";

    TasksModule tasksModule = new TasksModule();
    Injector context = Guice.createInjector(new ItestConfigModule()
        .override("management.topic.name", TT)
        .override("management.consumer.group.id", TT)
        .override("management.topic.rest.producer.name", TT)
        .override("topic.declarations.consumer.polls.max", 2)
        .override("topic.declarations.consumer.poll.timeout.ms", 1001),
        new se.yolean.kafka.topic.manager.kafkaadmin.Config().withTasks(tasksModule),
        new se.yolean.kafka.topic.manager.schemaregistry.Config().withTasks(tasksModule)
        );

    String restProxyUrl = context.getInstance(Key.get(String.class, Names.named("rest.proxy.url")));
    // See first how REST Proxy responds when a topic doesn't exist
    /* Ehhh... this seems to trigger an infinite loop in rest proxy, resulting in timeout here. Proxy logs:
     * WARN [Producer clientId=producer-2] Error while fetching metadata with correlation id 1 : {test-ManagedTopicHandlerIntegrationTest-1512308639065-newtopic1=UNKNOWN_TOPIC_OR_PARTITION} (org.apache.kafka.clients.NetworkClient:246)
    Future<Response> noTopicToPostToReq = ClientBuilder.newBuilder()
        .build()
        .target(restProxyUrl + "/topics/" + TN + "-newtopic1")
        .request("application/vnd.kafka.json.v2+json")
        .accept("application/vnd.kafka.v2+json")
        .async()
        .post(Entity.entity("{\"records\":[{\"value\":{\"foo\":\"bar\"}}]}", "application/vnd.kafka.json.v2+json"));
    Response noTopicToPostTo = noTopicToPostToReq.get(1003, TimeUnit.MILLISECONDS);
    assertNotEquals(200, noTopicToPostTo.getStatus());
    System.out.println(noTopicToPostTo.readEntity(String.class));
    assertEquals(404, noTopicToPostTo.getStatus());
     */

    // Configure the management topic
    ManagedTopicHandler handler = new ManagedTopicHandler(context, tasksModule);
    ManagementTopicDeclarationProvider mgmtProvider = context.getInstance(ManagementTopicDeclarationProvider.class);

    // Run init
    boolean result = handler.handle(mgmtProvider.get());
    assertTrue("Handler should return success", result);

    // Test that we can consume the configured management topic
    TopicsLogConsumer consumer = context.getInstance(TopicsLogConsumer.class);
    ManagedTopicHandler mockHandler = Mockito.mock(ManagedTopicHandler.class);
    consumer.setMessageHandler(mockHandler);
    consumer.run();

    // Now produce to the REST endpoint
    String newtopic1 = TN + "-newtopic1";
    JsonObject json = Json.createObjectBuilder()
        .add("records", Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("key", newtopic1)
                .add("value", Json.createObjectBuilder()
                    .add("name", newtopic1)
                )
            )
        )
        .build();
    System.out.println(json.toString());
    Future<Response> req = ClientBuilder.newBuilder()
        .connectTimeout(1002, TimeUnit.MILLISECONDS) // just testing, compare with .get below
        .build()
        .target(restProxyUrl + "/topics/" + TT)
        //.request(MediaType.)
        .request("application/vnd.kafka.json.v2+json")
        .accept("application/vnd.kafka.v2+json")
        .async()
        .post(Entity.entity(json.toString(), "application/vnd.kafka.json.v2+json"));
    Response restProxyResultJson = req.get(1003, TimeUnit.MILLISECONDS);
    System.out.println(restProxyResultJson.readEntity(String.class));
    assertEquals(200, restProxyResultJson.getStatus());

    // Run another consumer round and check that is invokes the topic handler
    consumer.run();
    ArgumentCaptor<ManagedTopic> toHandler = ArgumentCaptor.forClass(ManagedTopic.class);
    Mockito.verify(mockHandler, Mockito.times(1)).handle(toHandler.capture());
    ManagedTopic value = toHandler.getValue();
    assertEquals(newtopic1, value.getName());

    // End of this test. The topic handler has its own integration test.
  }

}
