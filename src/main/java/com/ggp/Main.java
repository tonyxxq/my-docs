package com.ggp;

import com.ggp.games.LeducPoker.GameDescription;
import com.ggp.games.LeducPoker.TextStateVisualizer;
import com.ggp.players.RandomPlayerFactory;
import com.ggp.players.deepstack.DeepstackPlayer;

public class Main {

    public static void main(String[] args) {
        GameManager manager = new GameManager(new RandomPlayerFactory(), new RandomPlayerFactory(), new GameDescription(10, 10));
        manager.setStateVisualizer(new TextStateVisualizer());
        manager.run();
    }
}
