package com.computermind.stateeither;

import java.util.function.Function;

/**
 * Stateless right value
 *
 * @param <L> the left type
 * @param <R> the right type
 */
public final class Right<L, R> extends E<L, R> {
    private R x;

    /**
     * Construct a new one right value
     *
     * @param right the right value
     */
    public Right(R right) {
        x = right;
    }

    /**
     * Static constructor.
     *
     * @param right the right value
     * @param <L> the left type
     * @param <R> the right type
     * @return a new one left value
     */
    public static <L, R> E<L, R> success(R right) {
        return new Right(right);
    }

    @Override
    <T> T either(Function<L, T> l, Function<R, T> r) {
        return r.apply(x);
    }
}
