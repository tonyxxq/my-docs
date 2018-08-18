package com.ggp.utils;

import java.util.function.BiFunction;

public class PlayerHelpers {
    public static int getOpponentId(int id) {
        return (id == 1) ? 2 : 1;
    }

    public static <T> T selectByPlayerId(int id, T player1Val, T player2Val) {
        if (id == 1) return player1Val;
        if (id == 2) return player2Val;
        return null;
    }

    public static <T, U> U callWithPlayerParams(int id, T myParam, T opponentParam, BiFunction<T, T, U> fn) {
        if (id == 1) {
            return fn.apply(myParam, opponentParam);
        } else {
            return fn.apply(opponentParam, myParam);
        }
    }
}
