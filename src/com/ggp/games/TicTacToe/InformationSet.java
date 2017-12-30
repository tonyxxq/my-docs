package com.ggp.games.TicTacToe;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InformationSet implements IInformationSet{
    private int[] field;
    private int owningPlayerId;
    private int turn;
    private int myFields;
    private int enemyFields;
    public static final int FIELD_UNKNOWN = 0;
    public static final int FIELD_MINE = 1;
    public static final int FIELD_ENEMY = 2;

    public InformationSet(int[] field, int owningPlayerId, int turn, int myFields, int enemyFields) {
        this.field = field;
        this.owningPlayerId = owningPlayerId;
        this.turn = turn;
        this.myFields = myFields;
        this.enemyFields = enemyFields;
    }

    @Override
    public List<IAction> getLegalActions() {
        ArrayList<IAction> ret = new ArrayList<>(field.length - myFields - enemyFields);
        for(int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                if (field[5*x + y] == FIELD_UNKNOWN) ret.add(Action.getAction(owningPlayerId, x, y));
            }
        }
        return ret;
    }

    @Override
    public IInformationSet next(IAction a) {
        if (!isLegal(a)) return null;
        Action _a = (Action) a;
        int x = _a.getX();
        int y = _a.getY();
        int[] f = Arrays.copyOf(field, field.length);
        f[5*x + y] = FIELD_MINE;
        return new InformationSet(f, owningPlayerId, turn + 1, myFields + 1, enemyFields);

    }

    @Override
    public IInformationSet applyPercept(IPercept p) {
        if (p == null || !(p instanceof Percept)) return null;
        Percept _p = (Percept) p;
        if (_p.isSuccessful()) return this;
        int x = _p.getLastAction().getX();
        int y = _p.getLastAction().getY();
        int[] f = Arrays.copyOf(field, field.length);
        f[5*x + y] = FIELD_ENEMY;
        return new InformationSet(f, owningPlayerId, turn, myFields - 1, enemyFields + 1);
    }

    @Override
    public boolean isLegal(IAction a) {
        if (a == null || !(a instanceof  Action)) return false;
        Action _a = (Action) a;
        if (_a.getRole() != owningPlayerId) return false;
        int x = _a.getX();
        int y = _a.getY();
        if (x < 0 || x >= 5 || y < 0 || y >= 5) return false;
        if (field[5*x + y] != FIELD_UNKNOWN) return false;
        return true;
    }

    protected boolean hasPlayerWon() {
        if (myFields < 5) return false;
        // check columns
        for(int x = 0; x < 5; ++x) {
            for(int y = 0; y < 5; ++y) {
                if (field[5*x + y] != FIELD_MINE) continue;
            }
            return true;
        }

        // check rows
        for(int y = 0; y < 5; ++y) {
            for(int x = 0; x < 5; ++x) {
                if (field[5*x + y] != FIELD_MINE) continue;
            }
            return true;
        }

        // check diagonals
        if (field[2*5 + 2] != FIELD_MINE) return false;
        {
            int i = 0;
            for(; i < 5; ++i) {
                if (field[i*5 + i] != FIELD_MINE) break;
            }
            if (i == 5) {
                return true;
            }
        }
        {
            int i = 0;
            for(; i < 5; ++i) {
                if (field[i*5 + (4-i)] != FIELD_MINE) break;
            }
            if (i == 5) {
                return true;
            }
        }
        return false;
    }

    protected int getFieldValue(int x, int y) {
        return field[5*x + y];
    }

    /**
     * Advance to next info set with percept provided right away.
     * @param _p
     * @return next info set
     */
    protected InformationSet nextWithPercept(Percept _p)
    {
        if (_p.isSuccessful()) return this;
        int x = _p.getLastAction().getX();
        int y = _p.getLastAction().getY();
        int[] f = Arrays.copyOf(field, field.length);
        f[5*x + y] = _p.isSuccessful() ? FIELD_MINE : FIELD_ENEMY;

        return new InformationSet(f, owningPlayerId, turn + 1, myFields + (_p.isSuccessful() ? 1 : 0), enemyFields + (_p.isSuccessful() ? 0 : 1));
    }
}
