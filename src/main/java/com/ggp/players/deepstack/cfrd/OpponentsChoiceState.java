package com.ggp.players.deepstack.cfrd;

import com.ggp.*;
import com.ggp.players.deepstack.cfrd.actions.FollowAction;
import com.ggp.players.deepstack.cfrd.actions.TerminateAction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OpponentsChoiceState implements ICompleteInformationState {
    private static final long serialVersionUID = 1L;
    private ICompleteInformationState origGameState;
    private int opponentId;
    private double opponentCFV;


    public OpponentsChoiceState(ICompleteInformationState origGameState, int opponentId, double opponentCFV) {
        this.origGameState = origGameState;
        this.opponentId = opponentId;
        this.opponentCFV = opponentCFV;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public int getActingPlayerId() {
        return opponentId;
    }

    @Override
    public double getPayoff(int player) {
        return 0;
    }

    @Override
    public List<IAction> getLegalActions() {
        return Arrays.asList(FollowAction.instance, TerminateAction.instance);
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        if (player != opponentId) return origGameState.getInfoSetForPlayer(player);
        return new OpponentsChoiceIS(opponentId, origGameState.getInfoSetForPlayer(opponentId));
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        if (a == null) return null;
        if (a.getClass() == FollowAction.class) {
            return origGameState;
        } else if (a.getClass() == TerminateAction.class) {
            return new OpponentsTerminalState(opponentCFV, opponentId);
        }
        return null;
    }

    @Override
    public Iterable<IPercept> getPercepts(IAction a) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public IRandomNode getRandomNode() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpponentsChoiceState that = (OpponentsChoiceState) o;
        return opponentId == that.opponentId &&
                Double.compare(that.opponentCFV, opponentCFV) == 0 &&
                Objects.equals(origGameState, that.origGameState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origGameState, opponentId, opponentCFV);
    }
}
