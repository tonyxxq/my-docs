package com.ggp.players.random;

import com.ggp.*;
import com.ggp.utils.RandomItemSelector;

public class RandomPlayer implements IPlayer {
    public static class Factory implements IPlayerFactory {
        @Override
        public IPlayer create(IGameDescription game, int role) {
            return new RandomPlayer(game.getInitialInformationSet(role), role);
        }
    }

    private IInformationSet infoSet;
    private int role;
    RandomItemSelector<IAction> randomActionSelector = new RandomItemSelector<>();

    public RandomPlayer(IInformationSet infoSet, int role) {
        this.infoSet = infoSet;
        this.role = role;
    }

    @Override
    public void init(long timeoutMillis) {
    }

    @Override
    public IAction act(long timeoutMillis) {
        IAction a = randomActionSelector.select(infoSet.getLegalActions());
        infoSet = infoSet.next(a);
        return a;
    }

    @Override
    public void forceAction(IAction a, long timeoutMillis) {
        infoSet = infoSet.next(a);
    }

    @Override
    public int getRole() {
        return role;
    }

    @Override
    public void receivePercepts(IPercept percept) {
        infoSet = infoSet.applyPercept(percept);
    }
}
