package com.computermind.stateeither;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

import static com.computermind.stateeither.Left.failure;
import static com.computermind.stateeither.Right.success;
import static java.util.Arrays.stream;

/**
 * Mutable State Either monad.
 *
 * @param <S> the context state
 * @param <L> type when left
 * @param <R> type when right
 */
public abstract class SE<S, L, R> {

    /**
     * The instance contains some left value
     *
     * @return true when left, false when right
     */
    public abstract boolean isLeft();

    /**
     * The instance contains some right value
     *
     * @return true when right, false when left
     */
    public abstract boolean isRight();

    /**
     * Get the current left value
     *
     * @return the left value
     * @throws IllegalStateException if the instance contains some right value
     */
    public abstract L left();

    /**
     * Get the current right value
     *
     * @return the right value
     * @throws IllegalStateException if the instance contains some left value
     */
    public abstract R right();

    /**
     * Get the state
     *
     * @return the state
     */
    public final S state() {
        return s;
    }

    /**
     * Chain one computation.
     *
     * @param k    the computation
     * @param <RR> the new right type
     * @return a new one stateful either
     */
    public abstract <RR> SE<S, L, RR> then(BiFunction<S, R, E<L, RR>> k);

    /**
     * Chain one computation.
     *
     * @param k    the computation
     * @param <RR> the new right type
     * @return a new one stateful either
     */
    public final <RR> SE<S, L, RR> then(Function<R, E<L, RR>> k) {
        return then((ignore, x) -> k.apply(x));
    }

    /**
     * Get a new one value from left or from right
     *
     * @param whenLeft  how to get the value when left
     * @param whenRight how to get the value when right
     * @param <T>       the returned type
     * @return the mapped value
     */
    public abstract <T> T either(BiFunction<S, L, T> whenLeft, BiFunction<S, R, T> whenRight);

    /**
     * Get a new one value from left or from right
     *
     * @param whenLeft  how to get the value when left
     * @param whenRight how to get the value when right
     * @param <T>       the returned type
     * @return the mapped value
     */
    public final <T> T either(Function<L, T> whenLeft, Function<R, T> whenRight) {
        return either((ignore, l) -> whenLeft.apply(l), (ignore, r) -> whenRight.apply(r));
    }

    /**
     * Get a new one value from left or from right
     *
     * @param whenLeft  how to get the value when left
     * @param whenRight how to get the value when right
     * @param <T>       the returned type
     * @return the mapped value
     */
    public final <T> T either(BiFunction<S, L, T> whenLeft, Function<R, T> whenRight) {
        return either(whenLeft, (ignore, r) -> whenRight.apply(r));
    }

    /**
     * Get a new one value from left or from right
     *
     * @param whenLeft  how to get the value when left
     * @param whenRight how to get the value when right
     * @param <T>       the returned type
     * @return the mapped value
     */
    public final <T> T either(Function<L, T> whenLeft, BiFunction<S, R, T> whenRight) {
        return either((ignore, l) -> whenLeft.apply(l), whenRight);
    }

    /**
     * Get a new one value from the state no matter if left or right
     *
     * @param whenAny how to get the value from the state
     * @param <T>     the returned type
     * @return the mapped value
     */
    public final <T> T either(Function<S, T> whenAny) {
        return either((s, ignore) -> whenAny.apply(s), (s, ignore) -> whenAny.apply(s));
    }

    /**
     * Do some left or right computation and return the same either instance
     *
     * @param whenLeft  action to do when left
     * @param whenRight action to do when right
     * @return the same stateful either instance
     */
    public final SE<S, L, R> with(BiConsumer<S, L> whenLeft, BiConsumer<S, R> whenRight) {
        either((s, l) -> {
            whenLeft.accept(s, l);
            return null;
        }, (s, r) -> {
            whenRight.accept(s, r);
            return null;
        });
        return this;
    }

    /**
     * Do some left or right computation and return the same either instance
     *
     * @param whenLeft  action to do when left
     * @param whenRight action to do when right
     * @return the same stateful either instance
     */
    public final SE<S, L, R> with(BiConsumer<S, L> whenLeft, Consumer<R> whenRight) {
        return with(whenLeft, (ignore, r) -> whenRight.accept(r));
    }

    /**
     * Do some left or right computation and return the same either instance
     *
     * @param whenLeft  action to do when left
     * @param whenRight action to do when right
     * @return the same stateful either instance
     */
    public final SE<S, L, R> with(Consumer<L> whenLeft, BiConsumer<S, R> whenRight) {
        return with((ignore, l) -> whenLeft.accept(l), whenRight);
    }

    /**
     * Do some left or right computation and return the same either instance
     *
     * @param whenLeft  action to do when left
     * @param whenRight action to do when right
     * @return the same stateful either instance
     */
    public final SE<S, L, R> with(Consumer<L> whenLeft, Consumer<R> whenRight) {
        return with((ignore, l) -> whenLeft.accept(l), (ignore, r) -> whenRight.accept(r));
    }

    /**
     * Map the right value
     *
     * @param f    mapping function
     * @param <RR> the new right type
     * @return the mapped stateful either
     */
    public abstract <RR> SE<S, L, RR> mapS(BiFunction<S, R, RR> f);

    /**
     * Map the right value
     *
     * @param f    mapping function
     * @param <RR> the new right type
     * @return the mapped stateful either
     */
    public final <RR> SE<S, L, RR> map(Function<R, RR> f) {
        return mapS((ignore, x) -> f.apply(x));
    }

    /**
     * Map the left value
     *
     * @param f    mapping function
     * @param <LL> the new left type
     * @return the mapped stateful either
     */
    public final <LL> SE<S, LL, R> mapSL(BiFunction<S, L, LL> f) {
        return isLeft() ? left(state(), f.apply(state(), left())) : right(state(), right());
    }

    /**
     * Map the left value
     *
     * @param f    mapping function
     * @param <LL> the new left type
     * @return the mapped stateful either
     */
    public final <LL> SE<S, LL, R> mapL(Function<L, LL> f) {
        return mapSL((ignore, l) -> f.apply(l));
    }

    private final S s;

    SE(S state) {
        s = state;
    }

    /**
     * Construct an stateless Either left value
     *
     * @param left the left value
     * @param <L>  the left type
     * @param <R>  the right type
     * @return one stateless left instance
     */
    public static <L, R> SE<Void, L, R> left(L left) {
        return new SELeft<>(null, left);
    }

    /**
     * Construct an stateless Either right value
     *
     * @param right the right value
     * @param <L>   the left type
     * @param <R>   the right type
     * @return one stateless right instance
     */
    public static <L, R> SE<Void, L, R> right(R right) {
        return new SERight<>(null, right);
    }

    /**
     * Construct an stateful Either left value
     *
     * @param state the state value
     * @param left  the left value
     * @param <L>   the left type
     * @param <R>   the right type
     * @return one stateless left instance
     */
    public static <S, L, R> SE<S, L, R> left(S state, L left) {
        return new SELeft<>(state, left);
    }

    /**
     * Construct an stateful Either right value
     *
     * @param state the state value
     * @param right the right value
     * @param <S>   the state class
     * @param <L>   the left type
     * @param <R>   the right type
     * @return one stateful right instance
     */
    public static <S, L, R> SE<S, L, R> right(S state, R right) {
        return new SERight<>(state, right);
    }

    /**
     * Construct an stateful Either right value
     *
     * @param state     the state value
     * @param right     the right value
     * @param leftClass the left class for type inference
     * @param <S>       the state class
     * @param <L>       the left type
     * @param <R>       the right type
     * @return one stateful right instance
     */
    public static <S, L, R> SE<S, L, R> right(S state, R right, Class<L> leftClass) {
        return right(state, right);
    }

    /**
     * Run a new computation (replacing the former) using the same context
     *
     * @param k    the computation
     * @param <RR> the resulting right type
     * @return the new SE
     */
    public <RR> SE<S, L, RR> run(Function<S, E<L, RR>> k) {
        return state(state(), k.apply(state()));
    }

    /**
     * Construct an stateful Either without relevant right value
     *
     * @param state initial state
     * @param <S>   the state type
     * @param <L>   the left type
     * @return one stateful right instance
     */
    public static <S, L> SE<S, L, Object> run(S state) {
        return right(state, null);
    }

    protected static <S, L, R> SE<S, L, R> state(S s, E<L, R> e) {
        return e.either(l -> left(s, l), r -> right(s, r));
    }

    /**
     * Consume strictly the input stream up to any Left value (or the whole stream if
     * no Left value exist)
     *
     * @param xs the stateful computations
     * @return the first left value or all rights
     */
    public SE<S, L, Stream<R>> chain(Stream<BiFunction<S, R, E<L, R>>> xs) {
        if (isLeft())
            return map(r -> null);

        final List<R> acc = new ArrayList<>();
        acc.add(right());

        final Iterator<BiFunction<S, R, E<L, R>>> i = xs.iterator();
        // x contains the previous right value
        SE<S, L, R> x = this;
        while (i.hasNext()) {
            x = x.then(i.next());
            if (x.isLeft())
                return x.map(r -> null);
            acc.add(x.right());
        }
        return x.map(r -> acc.stream());
    }

    /**
     * Consume strictly the input stream up to any Left value (or the whole stream if
     * no Left value exist)
     *
     * @param xs the stateful computations
     * @return the first left value or all rights
     */
    public <RR> SE<S, L, Stream<RR>> seq(Stream<Function<S, E<L, RR>>> xs) {
        return map(ignore -> (RR) null).chain(xs.map(k -> (s, ignore) -> k.apply(s)));
    }

    /**
     * Consume strictly the input stream up to any Left value (or the whole stream if
     * no Left value exist)
     *
     * @param xs the stateful computations
     * @return the first left value or all rights
     */
    public <RR> SE<S, L, Stream<RR>> seq(Function<S, E<L, RR>>... xs) {
        return seq(stream(xs));
    }

    /**
     * Consume strictly the whole input stream, chaining the state possible state transformations
     * and collecting all computation results
     *
     * @param xs the stateful computations
     * @return the SE result, the state is the last used state
     */
    public <RR> SE<S, L, Stream<SE<S, L, RR>>> scan(Stream<Function<S, E<L, RR>>> xs) {
        if (isLeft())
            return map(r -> null);

        final List<SE<S, L, RR>> acc = new ArrayList<>();
        final Iterator<Function<S, E<L, RR>>> i = xs.iterator();
        // x contains the previous SE value (chaining the state)
        SE<S, L, RR> x = map(r -> null);
        while (i.hasNext()) {
            x = x.run(i.next());
            acc.add(x);
        }
        return x.run(r -> success(acc.stream()));
    }

    /**
     * Consume all computations, chaining the state possible state transformations
     * and collecting all computation results
     *
     * @param xs the stateful computations
     * @return the SE result, the state is the last used state
     */
    public <RR> SE<S, L, Stream<SE<S, L, RR>>> scan(Function<S, E<L, RR>>... xs) {
        return scan(stream(xs));
    }

    /**
     * Consume computations until one right result.
     *
     * @param xs   the stateful computations
     * @param <RR> the resulting type
     * @return the SE result or all Left values
     */
    public <RR> SE<S, Stream<L>, RR> any(Stream<Function<S, E<L, RR>>> xs) {
        if (isLeft())
            return left(state(), Stream.empty());

        final List<L> acc = new ArrayList<>();
        final Iterator<Function<S, E<L, RR>>> i = xs.iterator();
        // x contains the last one SE value (chaining the state)
        SE<S, L, RR> x = map(r -> null);
        while (i.hasNext()) {
            x = x.run(i.next());
            if (x.isRight())
                return right(x.state(), x.right());
            else
                acc.add(x.left());
        }

        return left(x.state(), acc.stream());
    }

    /**
     * Consume computations until one right result.
     *
     * @param xs   the stateful computations
     * @param <RR> the resulting type
     * @return the SE result or all Left values
     */
    public <RR> SE<S, Stream<L>, RR> any(Function<S, E<L, RR>>... xs) {
        return any(stream(xs));
    }

    /**
     * Check one assertion
     *
     * @param trueOrFail assertion
     * @param error      error if fail
     * @return the SE result
     */
    public SE<S, L, R> guard_(BiFunction<S, R, Boolean> trueOrFail, BiFunction<S, R, L> error) {
        return then((s, r) -> trueOrFail.apply(s, r) ? success(r) : failure(error.apply(s, r)));

    }

    /**
     * Check one assertion
     *
     * @param trueOrFail assertion
     * @param error      error if fail
     * @return the SE result
     */
    public SE<S, L, R> guard_(BiFunction<S, R, Boolean> trueOrFail, Function<R, L> error) {
        return guard_(trueOrFail, (ignore, r) -> error.apply(r));

    }

    /**
     * Check one assertion
     *
     * @param trueOrFail assertion
     * @param error      error if fail
     * @return the SE result
     */
    public SE<S, L, R> guard_(Function<R, Boolean> trueOrFail, BiFunction<S, R, L> error) {
        return guard_((ignore, r) -> trueOrFail.apply(r), error);

    }

    /**
     * Check one assertion
     *
     * @param trueOrFail assertion
     * @param error      error if fail
     * @return the SE result
     */
    public SE<S, L, R> guard_(Function<R, Boolean> trueOrFail, Function<R, L> error) {
        return guard_((ignore, r) -> trueOrFail.apply(r), error);

    }

    /**
     * Check one assertion
     *
     * @param trueOrFail assertion
     * @param error      error if fail
     * @return the SE result
     */
    public SE<S, L, R> guard(Function<R, Boolean> trueOrFail, L error) {
        return guard_(trueOrFail, (ignore1, ignore2) -> error);

    }

    /**
     * Check one assertion
     *
     * @param trueOrFail assertion
     * @param error      error if fail
     * @return the SE result
     */
    public SE<S, L, R> guard(BiFunction<S, R, Boolean> trueOrFail, L error) {
        return guard_(trueOrFail, (ignore1, ignore2) -> error);

    }

    public <RR, X extends UnsafeException> SE<S, L, RR> mappingUnsafe(Class<X> clazz, Function<X, L> f, Function<SE<S, L, R>, SE<S, L, RR>> k) {
        try {
            return k.apply(this);
        } catch (UnsafeException e) {
            if (clazz.isInstance(e))
                return left(state(), f.apply((X) e));
            throw e;
        }
    }

    public <RR> SE<S, L, RR> mappingUnsafe(Function<UnsafeException, L> f, Function<SE<S, L, R>, SE<S, L, RR>> k) {
        return mappingUnsafe(UnsafeException.class, f, k);
    }

}

