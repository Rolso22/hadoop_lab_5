package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.http.javadsl.model.HttpRequest;
import akka.japi.pf.ReceiveBuilder;
import java.util.HashMap;

import static ru.bmstu.hadoop.labs.Constants.*;

public class CacheActor extends AbstractActor {
    HashMap<String, Float> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(String.class, this::getResult)
                .match(CacheMessage.class, this::saveResult)
                .build();
    }

    private void getResult(String url) {
        sender().tell(store.getOrDefault(url, DEFAULT_CACHE_NOT_FOUND), ActorRef.noSender());
    }

    private void saveResult(CacheMessage result) {
        
    }

}
