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
import org.asynchttpclient.*;
import static org.asynchttpclient.Dsl.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static ru.bmstu.hadoop.labs.Constants.*;

public class RouteFlow {
    private final ActorMaterializer materializer;
    private final ActorRef cacheActor;

    public RouteFlow(ActorSystem system, ActorMaterializer materializer) {
        this.materializer = materializer;
        cacheActor = system.actorOf(Props.create(CacheActor.class));
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class)

                .map(request -> new Pair<>(request.getUri().query().get(TEST_URL).get(),
                        Integer.parseInt(request.getUri().query().get(TEST_COUNT).get())))

                .mapAsync(DEFAULT_THREADS, this::getAverageTime)

                .map(response -> {
                    cacheActor.tell(new CachePut(response.first(), response.second().second(), response.second().first()), ActorRef.noSender());
                    return HttpResponse.create().withEntity(URL + response.first() + COUNT + response.second().first() + AVERAGE_TIME + response.second().second());
                });
    }

    private CompletionStage<Pair<String, Pair<Integer, Float>>> getAverageTime(Pair<String, Integer> request) {
        return Patterns.ask(cacheActor, new CacheGet(request.first(), request.second()), Duration.ofMillis(TIME_OUT_MILLIS))
                .thenCompose(answer -> {
                    if ((Float) answer != DEFAULT_CACHE_NOT_FOUND) {
                        return CompletableFuture.completedFuture(new Pair<>(request.first(), new Pair<>(request.second(), (Float) answer)));
                    } else {
                        return Source.from(Collections.singletonList(request))
                                .toMat(testSink(request), Keep.right())
                                .run(materializer)
                                .thenCompose(time -> CompletableFuture.completedFuture(new Pair<>(request.first(), new Pair<>(request.second(), ((float) time / request.second())))));
                    }
                });
    }

    private Sink<Pair<String, Integer>, CompletionStage<Long>> testSink(Pair<String, Integer> req) {
        return Flow.<Pair<String, Integer>>create()
                .mapConcat(mes -> Collections.nCopies(req.second(), req.first()))
                .mapAsync(req.second(), this::sendRequests)
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

    private CompletableFuture<Long> sendRequests(String url) throws ExecutionException, InterruptedException {
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        long start = new Date().getTime();
        CompletableFuture<Response> result = asyncHttpClient.prepareGet(url).execute().toCompletableFuture();
        return result.thenCompose(response -> {
            long end = new Date().getTime();
            return CompletableFuture.completedFuture(end - start);
        });
    }

}
