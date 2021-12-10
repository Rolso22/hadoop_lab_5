package ru.bmstu.hadoop.labs;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;
import static ru.bmstu.hadoop.labs.Constants.*;

public class StreamApp {

    public StreamApp() {}

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("Routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        StreamApp instance = new StreamApp();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                instance.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", DEFAULT_PORT),
                materializer
        );
        System.out.println(SERVER_ONLINE + DEFAULT_PORT);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return route(
                get(() -> parameter(PACKAGE_ID, (id) -> {
                    Future<Object> result = Patterns.ask(router, new GetRequest(id), TIME_OUT_MILLIS);
                    return completeOKWithFuture(result, Jackson.marshaller());
                })),
                post(() -> entity(Jackson.unmarshaller(TestPackage.class), msg -> {
                            router.tell(msg, ActorRef.noSender());
                            return complete(HAPPY_ANSWER);
                        })
                ));
    }

}
