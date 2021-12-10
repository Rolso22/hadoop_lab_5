package ru.bmstu.hadoop.labs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class StreamApp {

    

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("Routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AkkaApp instance = new AkkaApp();
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
}
