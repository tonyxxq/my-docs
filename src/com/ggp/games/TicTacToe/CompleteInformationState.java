package com.ggp.games.TicTacToe;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.List;

public class CompleteInformationState implements ICompleteInformationState {
    private InformationSet xInfoSet;
    private InformationSet oInfoSet;
    private boolean terminal;
    private int actingPlayer;
    private int xPayoff = 0;

    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;

    public CompleteInformationState(InformationSet xInfoSet, InformationSet oInfoSet, int actingPlayer) {
        this.xInfoSet = xInfoSet;
        this.oInfoSet = oInfoSet;
        this.actingPlayer = actingPlayer;
        detectTerminal();
    }

    @Override
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public boolean isRandomNode() {
        return false;
    }

    @Override
    public int getActingPlayerId() {
        return actingPlayer;
    }

    @Override
    public double getPayoff(int player) {
        if (!isTerminal()) return 0;
        if (player == PLAYER_X) return xPayoff;
        else return -xPayoff;
    }

    @Override
    public List<IAction> getLegalActions() {
        return getActingPlayerInfoSet().getLegalActions();
    }

    @Override
    public IInformationSet getInfoSetForActingPlayer() {
        return getActingPlayerInfoSet();
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        if (!isLegal(a)) return null;
        Percept p = (Percept) getPercept(a);
        InformationSet next = getActingPlayerInfoSet().nextWithPercept(p);
        InformationSet nextX, nextO;
        int nextPlayer;
        if (actingPlayer == PLAYER_X) {
            nextPlayer = PLAYER_O;
            nextX = next;
            nextO = oInfoSet;
        } else {
            nextPlayer = PLAYER_X;
            nextX = xInfoSet;
            nextO = oInfoSet;
        }
        return new CompleteInformationState(nextX, nextO, nextPlayer);
    }

    @Override
    public IPercept getPercept(IAction a) {
        if (!isLegal(a)) return null;
        Action _a = (Action) a;
        int x = _a.getX();
        int y = _a.getY();
        if (getNonActingPlayerInfoSet().getFieldValue(x, y) != InformationSet.FIELD_MINE) return new Percept(_a, true);
        return new Percept(_a, false);
    }

    @Override
    public boolean isLegal(IAction a) {
        return getActingPlayerInfoSet().isLegal(a);
    }

    private InformationSet getActingPlayerInfoSet() {
        if (actingPlayer == PLAYER_X) return xInfoSet;
        return oInfoSet;
    }

    private InformationSet getNonActingPlayerInfoSet() {
        if (actingPlayer == PLAYER_X) return oInfoSet;
        return xInfoSet;
    }

    private void detectTerminal() {
        if (actingPlayer != PLAYER_X) return;
        boolean xWon = xInfoSet.hasPlayerWon();
        boolean oWon = oInfoSet.hasPlayerWon();
        terminal = xWon || oWon;
        if (!terminal) return;
        if (xWon && oWon) {
            xPayoff = 0;
        } else if (xWon) {
            xPayoff = 1;
        } else {
            xPayoff = -1;
        }
    }
}
