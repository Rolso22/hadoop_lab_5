package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.http.javadsl.model.HttpRequest;
import akka.japi.pf.ReceiveBuilder;
import java.util.HashMap;

public class CacheActor extends AbstractActor {
    HashMap<String, Integer> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(CacheMessage.class, this::getResult)
                .build();
    }

    private void getResult(CacheMessage request) {
        String url = request.getUrl();
        sender().tell(store.getOrDefault(url, -1), ActorRef.noSender());
    }

}
