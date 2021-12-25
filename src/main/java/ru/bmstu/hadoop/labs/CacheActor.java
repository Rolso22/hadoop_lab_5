package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import java.util.HashMap;
import akka.japi.Pair;
import ru.bmstu.hadoop.labs.Messages.GetFromCache;
import ru.bmstu.hadoop.labs.Messages.PutToCache;

import static ru.bmstu.hadoop.labs.Constants.*;

public class CacheActor extends AbstractActor {
    HashMap<String, Pair<Integer, Float>> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetFromCache.class, this::getResult)
                .match(PutToCache.class, result -> store.put(result.getUrl(), new Pair<>(result.getCount(), result.getResult())))
                .build();
    }

    private void getResult(GetFromCache request) {
        String url = request.getUrl();
        if (store.containsKey(url) && store.get(url).first() == request.getCount()) {
            sender().tell(store.get(url).second(), ActorRef.noSender());
        } else {
            sender().tell(DEFAULT_CACHE_NOT_FOUND, ActorRef.noSender());
        }
    }

}
