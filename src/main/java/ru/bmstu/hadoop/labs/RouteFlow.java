package ru.bmstu.hadoop.labs;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.asynchttpclient.*;
import static org.asynchttpclient.Dsl.*;

import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import scala.concurrent.Future;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static ru.bmstu.hadoop.labs.Constants.*;

public class RouteFlow {
    private ActorSystem system;
    private ActorMaterializer materializer;
    private ActorRef cacheActor;

    public RouteFlow(ActorSystem system, ActorMaterializer materializer) {
        this.system = system;
        this.materializer = materializer;
        cacheActor = system.actorOf(Props.create(ActorSystem.class));
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class)
                .map(request -> {
                    return new Pair<String, Integer>(request.getUri().query().get(TEST_URL).get(),
                            Integer.parseInt(request.getUri().query().get(TEST_COUNT).get()));
                })
                .mapAsync(2, request -> {
                    CompletionStage<Object> result = Patterns.ask(cacheActor, new CacheMessage(request.first(), request.second()), Duration.ofMillis(TIME_OUT_MILLIS));
                    result.thenCompose(answer -> {
                        if ((Float) answer != DEFAULT_CACHE_NOT_FOUND) {
                            return CompletableFuture.completedFuture(answer);
                        } else {
                            Source.from(Collections.singletonList(request))
                                    .toMat(testSink(request), Keep.right())
                                    .run(materializer)
                                    .thenCompose(time -> CompletableFuture.completedFuture(time / request.second()));
                        }
                    })
                });
    }

    private Sink<Pair<String, Integer>, CompletionStage<Long>> testSink(Pair<String, Integer> req) {
        return Flow.<Pair<String, Integer>>create()
                .mapConcat(mes -> Collections.nCopies(req.second(), req.first()))
                .mapAsync(req.second(), this::sendRequests)
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

    private CompletableFuture<Long> sendRequests(String url) {
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        long start = new Date().getTime();
        ListenableFuture<Response> whenResponse = asyncHttpClient.prepareGet(url).execute();
        long end = new Date().getTime();
        return CompletableFuture.completedFuture(end - start);
    }

}
