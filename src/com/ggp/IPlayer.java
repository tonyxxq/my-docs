package com.ggp;

public interface IPlayer {
    IAction act();
    int getId();
    void receivePercepts(IPercept percept);
}