package com.ggp.games.TicTacToe;

import com.ggp.IAction;

public class Action implements IAction {
    private int role;
    private int x = 0;
    private int y = 0;

    public Action(int role, int x, int y) {
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

}
