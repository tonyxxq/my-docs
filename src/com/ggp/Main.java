package com.ggp;

import com.ggp.games.TicTacToe.GameDescription;
import com.ggp.players.RandomPlayerFactory;

public class Main {

    public static void main(String[] args) {
        GameManager manager = new GameManager(new RandomPlayerFactory(), new RandomPlayerFactory(), new GameDescription());
        manager.run();
        System.out.println("Finished: X: " + manager.getPayoff(1) + ", O: " + manager.getPayoff(2));
    }
}
