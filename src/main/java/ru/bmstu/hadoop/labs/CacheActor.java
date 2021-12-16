package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import java.util.HashMap;
import akka.japi.Pair;

import static ru.bmstu.hadoop.labs.Constants.*;

public class CacheActor extends AbstractActor {
    HashMap<String, Pair<Integer, Float>> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(String.class, url -> sender().tell(store.getOrDefault(url, DEFAULT_CACHE_NOT_FOUND), ActorRef.noSender()))
                .match(CacheMessage.class, result -> store.put(result.getUrl(), result.getResult()))
                .build();
    }

    private void gerResult()

}
