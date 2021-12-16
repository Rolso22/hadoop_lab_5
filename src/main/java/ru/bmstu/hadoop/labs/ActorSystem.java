package ru.bmstu.hadoop.labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import java.util.HashMap;

public class ActorSystem extends AbstractActor {

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                //.match()
                .build();
    }
}
