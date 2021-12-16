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
                    Pair<String, Integer> pair = new Pair<>(request.getUri().query().get(), )
                    request.getUri()
                })
    }

}
