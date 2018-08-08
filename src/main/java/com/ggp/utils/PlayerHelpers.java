package com.ggp.utils;

public class PlayerHelpers {
    public static int getOpponentId(int id) {
        return (id == 1) ? 2 : 1;
    }

    public static <T> T selectByPlayerId(int id, T player1Val, T player2Val) {
        if (id == 1) return player1Val;
        if (id == 2) return player2Val;
        return null;
    }
}
