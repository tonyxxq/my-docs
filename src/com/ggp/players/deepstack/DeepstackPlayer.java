package com.ggp.players.deepstack;

import com.ggp.*;

import java.util.ArrayList;
import java.util.List;

public class DeepstackPlayer implements IPlayer {
    private int id;
    private List<Double> range;
    private List<Double> opponentCFV;
    private int myHiddenInfo = 0; // index to opponents information set

    public DeepstackPlayer(int id) {
        this.id = id;
        range = new ArrayList<>(1);
        range.set(0, 1d);
        opponentCFV = new ArrayList<>(1);
        opponentCFV.set(0, 0d);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void initGame(IGameManager game, int role, IInformationSet initialInfoSet) {

    }

    private double cfr(ICompleteInformationState s, int player, int t, int p1, int p2) {
        if (s.isTerminal()) {
            return s.getPayoff(player);
        } else if (s.isRandomNode()) {
            //IAction a = s.sampleRandomAction();
            IAction a = null; // sample random action
            return cfr(s.next(a), player, t, p1, p2);
        }
        IInformationSet i = s.getInfoSetForActingPlayer();
        double v = 0;
        List<IAction> actions = s.getLegalActions();
        List<Double> va = new ArrayList<>(actions.size());
        int idx = 0;
        for (IAction a : actions) {
            if (s.getActingPlayerId() == 1) {
                va.set(idx, cfr(s.next(a), player, t, /* strategy(t, i,a)* */ p1, p2 ));
            } else if (s.getActingPlayerId() == 2) {
                va.set(idx, cfr(s.next(a), player, t, p1, /* strategy(t, i,a)* */ p2 ));
            }
            v = v + /* strategy(t, i,a)* */ va.get(idx);
                    ++idx;
        }
        // TODO
        return 0d;
    }

    @Override
    public IAction act() {
        return null;
    }

    @Override
    public void receivePercepts(IPercept percept) {

    }
}
