package com.computermind.stateeither;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.computermind.stateeither.Left.failure;
import static com.computermind.stateeither.Right.success;
import static com.computermind.stateeither.SE.left;
import static com.computermind.stateeither.SE.right;
import static com.computermind.stateeither.UnsafeException.unsafe1;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"unchecked"})
class SETest {
    @Test
    void then() {
        assertEquals("foo", right(1L).then(r -> success("foo")).right());
    }

    @Test
    void either() {
        assertEquals("foo", right(1L).either(r -> null, (s, r) -> "foo"));
        assertEquals("foo", left(1L).either((s, r) -> "foo", r -> null));
    }

    @Test
    void with() {

        final AtomicInteger x = new AtomicInteger(0);

        final BiConsumer<Void, Object> bi = (s, z) -> {
            x.incrementAndGet();
        };
        final BiConsumer<Void, Object> bn = (s, z) -> {
        };
        final Consumer<Object> ci = z -> {
            x.incrementAndGet();
        };
        final Consumer<Object> cn = z -> {
        };

        left(null).with(bi, bn);
        assertEquals(1, x.get());
        left(null).with(bi, cn);
        assertEquals(2, x.get());
        left(null).with(ci, bn);
        assertEquals(3, x.get());
        left(null).with(ci, cn);
        assertEquals(4, x.get());

        right(null).with(bn, bi);
        assertEquals(5, x.get());
        right(null).with(bn, ci);
        assertEquals(6, x.get());
        right(null).with(cn, bi);
        assertEquals(7, x.get());
        right(null).with(cn, ci);
        assertEquals(8, x.get());

    }

    @Test
    void state() {

        final List<String> xs = new ArrayList<>();

        final long r = SE.<List<String>, Long, Long>right(xs, 5L)
                .mapS((s, n) -> {
                    s.add(n.toString());
                    return 2 * n;
                })
                .then((s, n) -> {
                    s.add(n.toString());
                    return failure(3 + n);
                })
                .left();

        assertEquals(2 * 5L + 3, r);
        assertEquals("5", xs.get(0));
        assertEquals("10", xs.get(1));

    }

    private static Function<Random, E<String, String>> action(String name) {
        return rnd -> rnd.nextBoolean() ? success(name.toLowerCase()) : failure(name.toUpperCase());
    }

    @Test
    void sequence() {
        final int N = 10_000;
        final double ERROR = 1e-2;

        // run test and count cases
        final Map<String, Integer> counter = new HashMap<>();
        for (int i = 0; i < N; i++) {
            final String k = right(new Random(), null, String.class)
                    .seq(action("A"), action("B"), action("C"))
                    .scan(action("D"), action("E"))
                    .either(l -> l, xs -> xs.map(x -> x.either(l -> l, r -> r)).collect(joining()));
            counter.put(k, counter.getOrDefault(k, 0) + 1);
        }

        // check frequency quality
        final BiFunction<Double, String, Boolean> checkQA =
                (expected, key) -> {
                    final double value = counter.getOrDefault(key, 0) / (double) N;
                    return expected - ERROR <= value && value <= expected + ERROR;
                };

        assertEquals(7, counter.size());
        assertTrue(checkQA.apply(1 / 2.0, "A"));
        assertTrue(checkQA.apply(1 / 4.0, "B"));
        assertTrue(checkQA.apply(1 / 8.0, "C"));
        assertTrue(checkQA.apply(1 / (4 * 8.0), "de"));
        assertTrue(checkQA.apply(1 / (4 * 8.0), "De"));
        assertTrue(checkQA.apply(1 / (4 * 8.0), "dE"));
        assertTrue(checkQA.apply(1 / (4 * 8.0), "DE"));
    }

    @Test
    void any() {

        assertTrue(right(null).any().isLeft());

        final int N = 10_000;
        final double ERROR = 1e-2;

        // run test and count cases
        final Map<String, Integer> counter = new HashMap<>();
        for (int i = 0; i < N; i++) {
            final String k = right(new Random(), null, String.class)
                    .any(action("A"), action("B"), action("C"))
                    .mapL(xs -> xs.collect(joining()))
                    .either(l -> l, r -> r);
            counter.put(k, counter.getOrDefault(k, 0) + 1);
        }

        // check frequency quality
        final BiFunction<Double, String, Boolean> checkQA =
                (expected, key) -> {
                    final double value = counter.getOrDefault(key, 0) / (double) N;
                    return expected - ERROR <= value && value <= expected + ERROR;
                };

        assertEquals(4, counter.size());
        assertTrue(checkQA.apply(1 / (2.0 * 2.0 * 2.0), "ABC"));
        assertTrue(checkQA.apply(1 / 2.0, "a"));
        assertTrue(checkQA.apply(1 / (2.0 * 2.0), "b"));
        assertTrue(checkQA.apply(1 / (2.0 * 2.0 * 2.0), "c"));

    }

    @Test
    void withExceptions() {
        right((Void) null, "bad file", String.class)
                .mappingUnsafe(UnsafeException::getMessage, m -> m
                        .map(Paths::get)
                        .map(unsafe1(Files::lines)));
    }

}