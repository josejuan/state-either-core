package com.computermind.stateeither;

@FunctionalInterface
public interface UnsafeBiFunction<A, B, C> {
    C apply(A x, B y) throws Exception;
}
