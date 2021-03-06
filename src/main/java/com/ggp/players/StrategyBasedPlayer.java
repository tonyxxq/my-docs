package com.ggp.players;

import com.ggp.*;
import com.ggp.utils.PlayerHelpers;
import com.ggp.utils.random.RandomSampler;

public class StrategyBasedPlayer implements IPlayer {
    public static class Factory implements IPlayerFactory {
        private static final long serialVersionUID = 1L;
        private IStrategy strategy;

        public Factory(IStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public IPlayer create(IGameDescription game, int role) {
            return new StrategyBasedPlayer(role, game, strategy);
        }
    }

    private int myId;
    private IInformationSet hiddenInfo;
    private IStrategy strategy;
    private RandomSampler sampler = new RandomSampler();

    public StrategyBasedPlayer(int myId, IGameDescription gameDesc, IStrategy strategy) {
        this.myId = myId;
        this.hiddenInfo = gameDesc.getInitialInformationSet(myId);
        this.strategy = strategy;
    }

    @Override
    public void init(long timeoutMillis) {
    }

    @Override
    public IAction act(long timeoutMillis) {
        IAction a = PlayerHelpers.sampleAction(sampler, hiddenInfo, strategy);
        hiddenInfo = hiddenInfo.next(a);
        return a;
    }

    @Override
    public void forceAction(IAction a, long timeoutMillis) {
        hiddenInfo = hiddenInfo.next(a);
    }

    @Override
    public int getRole() {
        return myId;
    }

    @Override
    public void receivePercepts(IPercept percept) {
        hiddenInfo = hiddenInfo.applyPercept(percept);
    }
}
