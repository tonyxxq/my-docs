package com.ggp.games.TicTacToe;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InformationSet implements IInformationSet{
    private int[] field;
    private int owningPlayerId;
    private int turn;
    private int myFields;
    private int enemyFields;
    private Long comparisonKey;
    public static final int FIELD_UNKNOWN = 0;
    public static final int FIELD_MINE = 1;
    public static final int FIELD_ENEMY = 2;

    public InformationSet(int[] field, int owningPlayerId, int turn, int myFields, int enemyFields) {
        this.field = field;
        this.owningPlayerId = owningPlayerId;
        this.turn = turn;
        this.myFields = myFields;
        this.enemyFields = enemyFields;
        this.comparisonKey = computeComparisonKey();
    }

    public boolean hasLegalActions() {
        return myFields + enemyFields < field.length;
    }

    @Override
    public List<IAction> getLegalActions() {
        ArrayList<IAction> ret = new ArrayList<>(field.length - myFields - enemyFields);
        if (!hasLegalActions()) return ret;
        for(int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                if (field[5*x + y] == FIELD_UNKNOWN) ret.add(MarkFieldAction.getAction(owningPlayerId, x, y));
            }
        }
        return ret;
    }

    @Override
    public IInformationSet next(IAction a) {
        if (!isLegal(a)) return null;
        MarkFieldAction _a = (MarkFieldAction) a;
        int x = _a.getX();
        int y = _a.getY();
        int[] f = Arrays.copyOf(field, field.length);
        f[5*x + y] = FIELD_MINE;
        return new InformationSet(f, owningPlayerId, turn + 1, myFields + 1, enemyFields);

    }

    @Override
    public IInformationSet applyPercept(IPercept p) {
        if (p == null || !(p instanceof ActionSuccessPercept)) return null;
        ActionSuccessPercept _p = (ActionSuccessPercept) p;
        if (_p.isSuccessful()) return this;
        int x = _p.getLastAction().getX();
        int y = _p.getLastAction().getY();
        int[] f = Arrays.copyOf(field, field.length);
        f[5*x + y] = FIELD_ENEMY;
        return new InformationSet(f, owningPlayerId, turn, myFields - 1, enemyFields + 1);
    }

    @Override
    public boolean isLegal(IAction a) {
        if (a == null || !(a instanceof MarkFieldAction)) return false;
        MarkFieldAction _a = (MarkFieldAction) a;
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
            int y = 0;
            for(; y < 5; ++y) {
                if (field[5*x + y] != FIELD_MINE) break;
            }
            if (y == 5) return true;
        }

        // check rows
        for(int y = 0; y < 5; ++y) {
            int x = 0;
            for(; x < 5; ++x) {
                if (field[5*x + y] != FIELD_MINE) break;
            }
            if (x == 5) return true;
        }

        // check diagonals
        if (field[2*5 + 2] != FIELD_MINE) return false;
        {
            int i = 0;
            for(; i < 5; ++i) {
                if (field[i*5 + i] != FIELD_MINE) break;
            }
            if (i == 5) return true;
        }
        {
            int i = 0;
            for(; i < 5; ++i) {
                if (field[i*5 + (4-i)] != FIELD_MINE) break;
            }
            if (i == 5) return true;
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
    protected InformationSet nextWithPercept(ActionSuccessPercept _p)
    {
        int x = _p.getLastAction().getX();
        int y = _p.getLastAction().getY();
        int[] f = Arrays.copyOf(field, field.length);
        f[5*x + y] = _p.isSuccessful() ? FIELD_MINE : FIELD_ENEMY;

        return new InformationSet(f, owningPlayerId, turn + 1, myFields + (_p.isSuccessful() ? 1 : 0), enemyFields + (_p.isSuccessful() ? 0 : 1));
    }

    private long computeComparisonKey() {
        long sum = 0;
        long mul = 1;
        for (int i = 0; i < 25; ++i) {
            sum += field[i] * mul;
            mul *= 3;
        }
        if (owningPlayerId == CompleteInformationState.PLAYER_O) return -sum;
        return sum;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof InformationSet)) return -1;
        InformationSet s = (InformationSet) o;
        return comparisonKey.compareTo(s.comparisonKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformationSet that = (InformationSet) o;
        return Objects.equals(comparisonKey, that.comparisonKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparisonKey);
    }
}
