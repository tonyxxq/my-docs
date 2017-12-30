package com.ggp;

public interface IGameManager {
    /**
     * @return is finished?
     */
    boolean playOneTurn();
    boolean isFinished();
    void registerPlayer(int role, IPlayer player);
    int getPayoff(int role);
    ICompleteInformationState getInitialState();
}
