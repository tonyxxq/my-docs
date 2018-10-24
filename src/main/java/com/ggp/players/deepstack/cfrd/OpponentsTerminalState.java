package com.ggp.players.deepstack.cfrd;

import com.ggp.*;

import java.util.List;
import java.util.Objects;

public class OpponentsTerminalState implements ICompleteInformationState {
    private static final long serialVersionUID = 1L;
    private double opponentPayoff;
    private int opponentId;

    public OpponentsTerminalState(double opponentPayoff, int opponentId) {
        this.opponentPayoff = opponentPayoff;
        this.opponentId = opponentId;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public int getActingPlayerId() {
        return 0;
    }

    @Override
    public double getPayoff(int player) {
        return opponentPayoff * (player == opponentId ? 1 : -1);
    }

    @Override
    public List<IAction> getLegalActions() {
        return null;
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        return null;
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        return null;
    }

    @Override
    public Iterable<IPercept> getPercepts(IAction a) {
        return null;
    }

    @Override
    public IRandomNode getRandomNode() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpponentsTerminalState that = (OpponentsTerminalState) o;
        return Double.compare(that.opponentPayoff, opponentPayoff) == 0 &&
                opponentId == that.opponentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(opponentPayoff, opponentId);
    }
}
