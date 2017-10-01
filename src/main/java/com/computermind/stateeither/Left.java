package com.computermind.stateeither;

import java.util.function.Function;

/**
 * Stateless left value
 *
 * @param <L> the left type
 * @param <R> the right type
 */
public final class Left<L, R> extends E<L, R> {
    private L x;

    /**
     * Construct a new one left value
     *
     * @param left the left value
     */
    public Left(L left) {
        x = left;
    }

    /**
     * Static constructor.
     *
     * @param left the left value
     * @param <L> the left type
     * @param <R> the right type
     * @return a new one left value
     */
    public static <L, R> Left<L, R> failure(L left) {
        return new Left(left);
    }

    @Override
    <T> T either(Function<L, T> l, Function<R, T> r) {
        return l.apply(x);
    }
}
