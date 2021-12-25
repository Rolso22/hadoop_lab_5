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
import ru.bmstu.hadoop.labs.messages.GetFromCache;
import ru.bmstu.hadoop.labs.messages.PutToCache;

import static org.asynchttpclient.Dsl.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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

                .map(httpRequest -> new Pair<>(httpRequest.getUri().query().get(TEST_URL).get(),
                        Integer.parseInt(httpRequest.getUri().query().get(TEST_COUNT).get())))

                .mapAsync(DEFAULT_THREADS, this::getAverageTime)

                .map(response -> {
                    cacheActor.tell(new PutToCache(response.first(), response.second().second(), response.second().first()), ActorRef.noSender());
                    return HttpResponse.create().withEntity(URL + response.first() + COUNT + response.second().first() + AVERAGE_TIME + response.second().second());
                });
    }

    private CompletionStage<Pair<String, Pair<Integer, Float>>> getAverageTime(Pair<String, Integer> request) {
        return Patterns.ask(cacheActor, new GetFromCache(request.first(), request.second()), Duration.ofMillis(TIME_OUT_MILLIS))
                .thenCompose(answerFromCache -> {
                    if ((Float) answerFromCache != DEFAULT_CACHE_NOT_FOUND) {
                        return CompletableFuture.completedFuture(new Pair<>(request.first(), new Pair<>(request.second(), (Float) answerFromCache)));
                    } else {
                        return Source.from(Collections.singletonList(request))
                                .toMat(calculateAverageTime(request), Keep.right())
                                .run(materializer)
                                .thenCompose(averageTime -> CompletableFuture.completedFuture(new Pair<>(request.first(), new Pair<>(request.second(), ((float) averageTime / request.second())))));
                    }
                });
    }

    private Sink<Pair<String, Integer>, CompletionStage<Long>> calculateAverageTime(Pair<String, Integer> request) {
        return Flow.<Pair<String, Integer>>create()
                .mapConcat(msg -> Collections.nCopies(request.second(), request.first()))
                .mapAsync(request.second(), this::sendRequests)
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

    private CompletableFuture<Long> sendRequests(String url) {
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        long requestTime = new Date().getTime();
        CompletableFuture<Response> result = asyncHttpClient.prepareGet(url).execute().toCompletableFuture();
        return result.thenCompose(response -> {
            long responseTime = new Date().getTime();
            return CompletableFuture.completedFuture(responseTime - requestTime);
        });
    }

}
