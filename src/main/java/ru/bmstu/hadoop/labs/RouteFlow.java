package ru.bmstu.hadoop.labs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;

import java.util.concurrent.CompletionStage;
import static ru.bmstu.hadoop.labs.Constants.*;

public class RouteFlow {
    private ActorSystem system;
    private ActorMaterializer materializer;

    public RouteFlow(ActorSystem system, ActorMaterializer materializer) {
        this.system = system;
        this.materializer = materializer;
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class)
                .map(request -> {
                    return new Pair<String, Integer>(request.getUri().query().get(TEST_URL).get(),
                            Integer.parseInt(request.getUri().query().get(TEST_COUNT).get()));
                })
                .mapAsync(2, request -> {

                });
    }

}
