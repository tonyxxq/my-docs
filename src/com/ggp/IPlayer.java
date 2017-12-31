package com.ggp;

public interface IPlayer {
    void initGame(IGameManager game, int role, IInformationSet initialInfoSet);
    IAction act();
    int getId();
    void receivePercepts(IPercept percept);
}