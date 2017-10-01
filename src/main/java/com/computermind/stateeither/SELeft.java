package com.computermind.stateeither;

import java.util.function.BiFunction;

final class SELeft<S, L, R> extends SE<S, L, R> {
    private final L x;

    SELeft(S s, L x) {
        super(s);
        this.x = x;
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public L left() {
        return x;
    }

    @Override
    public R right() {
        throw new IllegalStateException("cannot get the right value from a left value");
    }

    @Override
    public <RR> SE<S, L, RR> then(BiFunction<S, R, E<L, RR>> k) {
        return new SELeft(state(), x);
    }

    @Override
    public <T> T either(BiFunction<S, L, T> whenLeft, BiFunction<S, R, T> whenRight) {
        return whenLeft.apply(state(), x);
    }

    @Override
    public <RR> SE<S, L, RR> mapS(BiFunction<S, R, RR> f) {
        return new SELeft<>(state(), x);
    }

}
