package com.ggp.games.TicTacToe;

import com.ggp.*;

public class GameManager implements IGameManager {
    private IPlayer xPlayer;
    private IPlayer oPlayer;
    private CompleteInformationState state;
    private CompleteInformationState initialState;

    public GameManager() {
        int field[] = {
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
        };
        InformationSet xInfoSet = new InformationSet(field, CompleteInformationState.PLAYER_X, 0, 0, 0);
        InformationSet oInfoSet = new InformationSet(field, CompleteInformationState.PLAYER_O, 0, 0, 0);
        CompleteInformationState s = new CompleteInformationState(xInfoSet, oInfoSet, CompleteInformationState.PLAYER_X);
        this.initialState = this.state = s;
    }

    @Override
    public boolean playOneTurn() {
        if (xPlayer == null || oPlayer == null) return true;
        if (state.isTerminal()) return true;
        IAction a;
        int turn = state.getActingPlayerId();
        if (turn == CompleteInformationState.PLAYER_X) {
            a = xPlayer.act();
        } else {
            a = oPlayer.act();
        }
        IPercept p = state.getPercept(a);
        state = (CompleteInformationState) state.next(a);
        if (turn == CompleteInformationState.PLAYER_X) {
            xPlayer.receivePercepts(p);
        } else {
            oPlayer.receivePercepts(p);
        }
        return state.isTerminal();
    }

    @Override
    public boolean isFinished() {
        return state.isTerminal();
    }

    @Override
    public void registerPlayer(int role, IPlayer player) {
        if (role == CompleteInformationState.PLAYER_X) xPlayer = player;
        if (role == CompleteInformationState.PLAYER_O) oPlayer = player;
        player.initGame(this, role, initialState.getInformationSet(role));
    }

    @Override
    public int getPayoff(int role) {
        return (int) state.getPayoff(role);
    }

    @Override
    public ICompleteInformationState getInitialState() {
        return initialState;
    }
}
