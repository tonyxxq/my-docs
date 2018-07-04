package com.ggp;

public interface IGameDescription {
    ICompleteInformationState getInitialState();
    default IInformationSet getInitialInformationSet(int role) {
        return getInitialState().getInfoSetForPlayer(role);
    }
    ICompleteInformationStateFactory getCISFactory();
}
