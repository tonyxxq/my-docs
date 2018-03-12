package com.ggp;

import java.util.List;

public interface ICompleteInformationState {
    boolean isTerminal();
    boolean isRandomNode();
    int getActingPlayerId();
    double getPayoff(int player);
    List<IAction> getLegalActions();
    IInformationSet getInfoSetForActingPlayer();
    ICompleteInformationState next(IAction a);
    Iterable<IPercept> getPercepts(IAction a);
    boolean isLegal(IAction a);

}
