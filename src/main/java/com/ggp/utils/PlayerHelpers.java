package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;
import com.ggp.utils.random.RandomSampler;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerHelpers {
    public static int getOpponentId(int id) {
        return (id == 1) ? 2 : 1;
    }

    public static <T> T selectByPlayerId(int id, T player1Val, T player2Val) {
        if (id == 1) return player1Val;
        if (id == 2) return player2Val;
        throw new IllegalArgumentException("id must be 1 or 2");
    }

    /**
     * Calls fn(P1, P2)
     * @param id
     * @param myParam
     * @param opponentParam
     * @param fn
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> U callWithOrderedParams(int id, T myParam, T opponentParam, BiFunction<T, T, U> fn) {
        if (id == 1) {
            return fn.apply(myParam, opponentParam);
        } else if (id == 2) {
            return fn.apply(opponentParam, myParam);
        } else {
            throw new IllegalArgumentException("id must be 1 or 2");
        }

    }

    /**
     * Calls fn(P1, P2)
     * @param id
     * @param myParam
     * @param opponentParam
     * @param fn
     * @param <T>
     * @return
     */
    public static <T> void callWithOrderedParamsVoid(int id, T myParam, T opponentParam, BiConsumer<T, T> fn) {
        if (id == 1) {
            fn.accept(myParam, opponentParam);
        } else if (id == 2) {
            fn.accept(opponentParam, myParam);
        } else {
            throw new IllegalArgumentException("id must be 1 or 2");
        }
    }

    /**
     * Calls fn(idParam)
     * @param id
     * @param player1Param
     * @param player2Param
     * @param fn
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> U callWithSelectedParam(int id, T player1Param, T player2Param, Function<T, U> fn) {
        return fn.apply(selectByPlayerId(id, player1Param, player2Param));
    }

    /**
     * Calls fn(idParam)
     * @param id
     * @param player1Param
     * @param player2Param
     * @param fn
     * @param <T>
     * @return
     */
    public static <T> void callWithSelectedParamVoid(int id, T player1Param, T player2Param, Consumer<T> fn) {
        fn.accept(selectByPlayerId(id, player1Param, player2Param));
    }

    public static IAction sampleAction(RandomSampler sampler, IInformationSet is, IStrategy strategy) {
        return sampler.select(is.getLegalActions(), action -> strategy.getProbability(is, action)).getResult();
    }
}
