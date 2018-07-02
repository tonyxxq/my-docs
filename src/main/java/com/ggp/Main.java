package com.ggp;

import com.ggp.games.TicTacToe.GameDescription;
import com.ggp.games.TicTacToe.TextStateVisualizer;
import com.ggp.players.RandomPlayerFactory;
import com.ggp.players.deepstack.DeepstackPlayer;

public class Main {

    public static void main(String[] args) {
        GameManager manager = new GameManager(new RandomPlayerFactory(), new DeepstackPlayer.Factory(), new GameDescription());
        manager.setStateVisualizer(new TextStateVisualizer());
        manager.run();
        System.out.println("Finished: X: " + manager.getPayoff(1) + ", O: " + manager.getPayoff(2));
    }
}
