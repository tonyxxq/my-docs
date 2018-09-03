package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.List;

/**
 * Base class for CIS wrappers, delegates most methods to the original state.
 */
public abstract class CompleteInformationStateWrapper implements ICompleteInformationState {
    protected ICompleteInformationState state;

    public CompleteInformationStateWrapper(ICompleteInformationState state) {
        this.state = state;
    }

    @Override
    public boolean isTerminal() {
        return state.isTerminal();
    }

    @Override
    public int getActingPlayerId() {
        return state.getActingPlayerId();
    }

    @Override
    public double getPayoff(int player) {
        return state.getPayoff(player);
    }

    @Override
    public List<IAction> getLegalActions() {
        return state.getLegalActions();
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        return state.getInfoSetForPlayer(player);
    }

    @Override
    public abstract ICompleteInformationState next(IAction a);

    @Override
    public Iterable<IPercept> getPercepts(IAction a) {
        return state.getPercepts(a);
    }

    @Override
    public boolean isLegal(IAction a) {
        return state.isLegal(a);
    }

    public ICompleteInformationState getOrigState() {
        return state;
    }
}
