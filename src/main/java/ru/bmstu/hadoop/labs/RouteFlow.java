package ru.bmstu.hadoop.labs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class RouteFlow {
    private ActorSystem system;
    private ActorMaterializer materializer;

    public RouteFlow(ActorSystem system, ActorMaterializer materializer) {
        this.system = system;
        this.materializer = materializer;
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {

    }

}
