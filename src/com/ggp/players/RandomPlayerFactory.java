package com.ggp.players;

import com.ggp.IGameDescription;
import com.ggp.IPlayer;
import com.ggp.IPlayerFactory;

public class RandomPlayerFactory implements IPlayerFactory {
    @Override
    public IPlayer create(IGameDescription game, int role) {
        return new RandomPlayer(game.getInitialInformationSet(role), role);
    }
}
