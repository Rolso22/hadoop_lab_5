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
                .match(String.class, url -> sender().tell(store.getOrDefault(url, DEFAULT_CACHE_NOT_FOUND), ActorRef.noSender()))
                .match(CacheMessage.class, result -> store.put(result.getUrl(), result.getResult()))
                .build();
    }
}
