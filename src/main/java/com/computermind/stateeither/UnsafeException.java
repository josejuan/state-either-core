package com.computermind.stateeither;

import java.util.function.BiFunction;
import java.util.function.Function;

public class UnsafeException extends RuntimeException {

    public UnsafeException() {
        super();
    }

    public UnsafeException(String message) {
        super(message);
    }

    public UnsafeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsafeException(Throwable cause) {
        super(cause);
    }

    public static <A, B> Function<A, B> unsafe(UnsafeFunction<A, B> f) {
        return a -> {
            try {
                return f.apply(a);
            } catch (Exception e) {
                throw new UnsafeException(e);
            }
        };
    }

    public static <A, B, C> BiFunction<A, B, C> unsafe(UnsafeBiFunction<A, B, C> f) {
        return (a, b) -> {
            try {
                return f.apply(a, b);
            } catch (Exception e) {
                throw new UnsafeException(e);
            }
        };
    }

    public static <A, B> Function<A, B> unsafe1(UnsafeFunction<A, B> f) {
        return unsafe(f);
    }

    public static <A, B, C> BiFunction<A, B, C> unsafe2(UnsafeBiFunction<A, B, C> f) {
        return unsafe(f);
    }
}

