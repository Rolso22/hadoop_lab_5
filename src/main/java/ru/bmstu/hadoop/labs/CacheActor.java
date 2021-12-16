package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.http.javadsl.model.HttpRequest;
import akka.japi.pf.ReceiveBuilder;
import java.util.HashMap;

public class CacheActor extends AbstractActor {
    

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(HttpRequest.class, this::getResult)
                .build();
    }

    private void getResult(HttpRequest request) {

    }

}
