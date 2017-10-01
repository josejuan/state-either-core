package com.computermind.stateeither;

import java.util.function.Function;

/**
 * Stateless Either
 *
 * @param <L> the left type
 * @param <R> the right type
 */
public abstract class E<L, R> {
    abstract <T> T either(Function<L, T> l, Function<R, T> r);
}
