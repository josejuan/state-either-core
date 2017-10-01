package com.computermind.stateeither;

@FunctionalInterface
public interface UnsafeFunction<A, B> {
    B apply(A x) throws Exception;
}
