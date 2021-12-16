package ru.bmstu.hadoop.labs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import static ru.bmstu.hadoop.labs.Constants.*;

public class StreamApp {

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("Routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        RouteFlow route = new RouteFlow(system, materializer);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.createFlow();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(DEFAULT_HOST, DEFAULT_PORT),
                materializer
        );
        System.out.println(SERVER_ONLINE + DEFAULT_PORT);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

}
