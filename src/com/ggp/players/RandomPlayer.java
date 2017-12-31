package com.ggp.players;

import com.ggp.*;

import java.util.List;
import java.util.Random;

public class RandomPlayer implements IPlayer {
    private IInformationSet infoSet;
    private int role;
    private Random rng = new Random();

    @Override
    public void initGame(IGameManager game, int role, IInformationSet initialInfoSet) {
        this.role = role;
        this.infoSet = initialInfoSet;
    }

    @Override
    public IAction act() {
        List<IAction> actions = infoSet.getLegalActions();
        if (actions == null || actions.isEmpty()) return null;
        IAction a = actions.get(rng.nextInt(actions.size()));
        infoSet = infoSet.next(a);
        System.out.println("Playing " + a.toString());
        return a;
    }

    @Override
    public int getId() {
        return role;
    }

    @Override
    public void receivePercepts(IPercept percept) {
        infoSet = infoSet.applyPercept(percept);
    }
}
