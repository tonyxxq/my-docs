package com.ggp;

public interface IGameDescription {
    ICompleteInformationState getInitialState();
    IInformationSet getInitialInformationSet(int role);
    boolean hasRandomPlayer();
}
