package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.http.javadsl.model.HttpRequest;
import akka.japi.pf.ReceiveBuilder;
import com.sun.xml.internal.ws.util.CompletedFuture;


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
        if (store.containsKey(url)) {
            CompletedFuture<Integer> result = 
            sender().tell(store.get(url), ActorRef.noSender());
        } else {
            sender().tell("no cache", ActorRef.noSender());
        }
    }

}
