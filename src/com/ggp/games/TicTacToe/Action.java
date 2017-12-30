package com.ggp.games.TicTacToe;

import com.ggp.IAction;

public class Action implements IAction {
    private static Action[] xActions = new Action[25];
    private static Action[] oActions = new Action[25];
    static {
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                xActions[5*x + y] = new Action(CompleteInformationState.PLAYER_X, x, y);
                oActions[5*x + y] = new Action(CompleteInformationState.PLAYER_O, x, y);
            }
        }
    }
    private int role;
    private int x = 0;
    private int y = 0;

    private Action(int role, int x, int y) {
        this.role = role;
        this.x = x;
        this.y = y;
    }

    public int getRole() {
        return role;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected static Action getAction(int role, int x, int y) {
        if (role == CompleteInformationState.PLAYER_X) {
            return xActions[5*x + y];
        } else {
            return oActions[5*x + y];
        }
    }
}
