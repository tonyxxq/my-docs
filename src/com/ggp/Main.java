package com.ggp;

import com.ggp.games.TicTacToe.GameManager;
import com.ggp.players.RandomPlayer;

public class Main {

    public static void main(String[] args) {
        IPlayer x = new RandomPlayer();
        IPlayer o = new RandomPlayer();
        IGameManager game = new GameManager();
        game.registerPlayer(1, x);
        game.registerPlayer(2, o);
        while(!game.playOneTurn()) {}
        System.out.println("Finished: X: " + game.getPayoff(1) + ", O: " + game.getPayoff(2));
    }
}
