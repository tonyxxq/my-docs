package com.ggp;

public interface IPlayer {
    IAction act();
    int getRole();
    void receivePercepts(IPercept percept);
}