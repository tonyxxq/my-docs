package com.ggp.games.TicTacToe;

import com.ggp.IAction;

import java.io.IOException;
import java.util.Objects;

public class MarkFieldAction implements IAction {
    private static final long serialVersionUID = 1L;
    public static class Cache {
        private static MarkFieldAction[] xActions;
        private static MarkFieldAction[] oActions;
        static {
            xActions = new MarkFieldAction[25];
            oActions = new MarkFieldAction[25];
            for (int x = 0; x < 5; ++x) {
                for (int y = 0; y < 5; ++y) {
                    xActions[5*x + y] = new MarkFieldAction(CompleteInformationState.PLAYER_X, x, y);
                    oActions[5*x + y] = new MarkFieldAction(CompleteInformationState.PLAYER_O, x, y);
                }
            }
        }

        protected static MarkFieldAction getAction(int role, int x, int y) {
            if (role == CompleteInformationState.PLAYER_X) {
                return xActions[5*x + y];
            } else {
                return oActions[5*x + y];
            }
        }
    }

    private int role;
    private int x;
    private int y;
    private transient String comparisonKey;
    private static String[] roleKeys = {"X", "O"};

    private MarkFieldAction(int role, int x, int y) {
        this.role = role;
        this.x = x;
        this.y = y;

        initComparisonKey();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initComparisonKey();
    }

    private void initComparisonKey() {
        comparisonKey = String.join(roleKeys[role-1], ";", Integer.toHexString(x), ";",  Integer.toHexString(y));
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

    @Override
    public String toString() {
        String mark = role == CompleteInformationState.PLAYER_X ? "X" : "O";
        return mark + " -> [" + x + ", " + y + "]";
    }

    @Override
    public int hashCode() {
        return comparisonKey.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkFieldAction that = (MarkFieldAction) o;
        return Objects.equals(comparisonKey, that.comparisonKey);
    }
}
