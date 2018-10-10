package com.ggp;

import java.io.Serializable;

public interface IGameDescription extends Serializable {
    ICompleteInformationState getInitialState();
    default IInformationSet getInitialInformationSet(int role) {
        return getInitialState().getInfoSetForPlayer(role);
    }
    ICompleteInformationStateFactory getCISFactory();
}
