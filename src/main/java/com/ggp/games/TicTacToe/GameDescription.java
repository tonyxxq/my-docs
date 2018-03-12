package com.ggp.games.TicTacToe;

import com.ggp.ICompleteInformationState;
import com.ggp.IGameDescription;
import com.ggp.IInformationSet;

public class GameDescription implements IGameDescription {
    private static CompleteInformationState initialState;
    static {
        int field[] = {
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
        };
        InformationSet xInfoSet = new InformationSet(field, CompleteInformationState.PLAYER_X, 0, 0, 0);
        InformationSet oInfoSet = new InformationSet(field, CompleteInformationState.PLAYER_O, 0, 0, 0);
        initialState = new CompleteInformationState(xInfoSet, oInfoSet, CompleteInformationState.PLAYER_X);
    }

    @Override
    public ICompleteInformationState getInitialState() {
        return initialState;
    }

    @Override
    public IInformationSet getInitialInformationSet(int role) {
        return initialState.getInformationSet(role);
    }

    @Override
    public boolean hasRandomPlayer() {
        return false;
    }
}
