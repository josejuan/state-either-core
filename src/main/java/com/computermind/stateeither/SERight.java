package com.computermind.stateeither;

import java.util.function.BiFunction;

final class SERight<S, L, R> extends SE<S, L, R> {
    private final R x;

    SERight(S s, R x) {
        super(s);
        this.x = x;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public L left() {
        throw new IllegalStateException("cannot get the left value from a right value");
    }

    @Override
    public R right() {
        return x;
    }

    @Override
    public <RR> SE<S, L, RR> then(BiFunction<S, R, E<L, RR>> k) {
        return state(state(), k.apply(state(), x));
    }

    @Override
    public <T> T either(BiFunction<S, L, T> whenLeft, BiFunction<S, R, T> whenRight) {
        return whenRight.apply(state(), x);
    }

    @Override
    public <RR> SE<S, L, RR> mapS(BiFunction<S, R, RR> f) {
        return new SERight<>(state(), f.apply(state(), x));
    }

}
