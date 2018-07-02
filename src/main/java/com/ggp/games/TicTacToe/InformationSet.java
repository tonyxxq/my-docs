package com.ggp.games.TicTacToe;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.*;

public class InformationSet implements IInformationSet{
    private int[] field;
    private int owningPlayerId;
    private int turn;
    private int myFields;
    private int knownEnemyFields;
    private Long comparisonKey;
    public static final int FIELD_UNKNOWN = 0;
    public static final int FIELD_MINE = 1;
    public static final int FIELD_ENEMY = 2;

    public InformationSet(int[] field, int owningPlayerId, int turn, int myFields, int knownEnemyFields) {
        this.field = field;
        this.owningPlayerId = owningPlayerId;
        this.turn = turn;
        this.myFields = myFields;
        this.knownEnemyFields = knownEnemyFields;
        this.comparisonKey = computeComparisonKey();
    }

    public boolean hasLegalActions() {
        return myFields + knownEnemyFields < field.length;
    }

    @Override
    public List<IAction> getLegalActions() {
        ArrayList<IAction> ret = new ArrayList<>(field.length - myFields - knownEnemyFields);
        if (!hasLegalActions()) return ret;
        for(int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                if (field[5*x + y] == FIELD_UNKNOWN) ret.add(MarkFieldAction.Cache.getAction(owningPlayerId, x, y));
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
        return new InformationSet(f, owningPlayerId, turn + 1, myFields + 1, knownEnemyFields);

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
        return new InformationSet(f, owningPlayerId, turn, myFields - 1, knownEnemyFields + 1);
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

        return new InformationSet(f, owningPlayerId, turn + 1, myFields + (_p.isSuccessful() ? 1 : 0), knownEnemyFields + (_p.isSuccessful() ? 0 : 1));
    }

    @Override
    public Iterator<ICompleteInformationState> iterator() {
        return new Iterator<ICompleteInformationState>() {
            private final int minOpponentFields;
            private final int maxOpponentFields;
            private int marking;
            private int[] marked; // fields marked by the enemy -> idx to freeFields
            private int[] revealed; // fields marked by us and discovered by the enemy -> idx to freeFields
            private final int[] baseField = new int[25];
            private final int[] freeFields = new int[25 - knownEnemyFields];
            private int[] revealableFields;
            private final int totalOp; // no. of mark + reveal operations to do
            private boolean hasNext = true;

            {
                maxOpponentFields = Math.min(turn, 25 - myFields) - knownEnemyFields;
                int min;
                if (owningPlayerId == CompleteInformationState.PLAYER_O) {
                    min = Math.max(1 - knownEnemyFields, 0); // X plays first
                    totalOp = 2*turn + 1;
                } else {
                    min = 0;
                    totalOp = 2*turn;
                }
                marked = new int[totalOp];
                revealed = new int[totalOp];
                for (int i = 0; i < totalOp; ++i) {
                    marked[i] = revealed[i] = -1;
                }
                minOpponentFields = min; // X plays first
                marking = minOpponentFields;
                int f = 0;
                for (int i = 0; i < 25; ++i) {
                    if (field[i] == FIELD_ENEMY) {
                        baseField[i] = FIELD_MINE;
                    } else {
                        baseField[i] = FIELD_UNKNOWN;
                        freeFields[f++] = i;
                    }
                }
                f = 0;
                for (int i = 0; i < marking; ++i) {
                    marked[i] = f++;
                }
                revealableFields = new int[freeFields.length - marking];
                for (int i = 0; i < freeFields.length - marking; ++i) {
                    revealableFields[i] = freeFields[f + i];
                }
                f = 0;
                for (int i = 0; i < totalOp - marking; ++i) {
                    revealed[i] = f++;
                }
            }

            private void resetRevealableFields() {
                int r = 0;
                int m = 0;
                for (int j = 0; j < freeFields.length; ++j) {
                    if (m >= marking) {
                        revealableFields[r++] = j;
                        continue;
                    }
                    if (j == marked[m]) {
                        m++;
                        continue;
                    }
                    revealableFields[r++] = j;
                }
            }

            private void resetArray(int[] arr, int start, int limit) {
                int f = start > 0 ? arr[start - 1] : 0;
                for (int i = start; i < limit; ++i) {
                    arr[i] = f++;
                }
            }

            private void resetRevealed(int start) {
                resetArray(revealed, start, totalOp - marking);
            }

            private void resetMarked(int start) {
                resetArray(marked, start, marking);
                resetRevealableFields();
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public ICompleteInformationState next() {
                // construct CIS from current iterator state
                InformationSet myInfoSet = InformationSet.this;
                int enemyId = (owningPlayerId == CompleteInformationState.PLAYER_X ? CompleteInformationState.PLAYER_O : CompleteInformationState.PLAYER_X);
                int enemyField[] = baseField.clone();
                int revealing = totalOp - marking;
                for (int i = 0; i < marking; ++i) {
                    enemyField[freeFields[marked[i]]] = FIELD_MINE;
                }
                for (int i = 0; i < revealing; ++i) {
                    enemyField[revealableFields[revealed[i]]] = FIELD_ENEMY;
                }
                InformationSet enemyInfoSet = new InformationSet(enemyField, enemyId, turn, marking, revealing);
                InformationSet xInfoSet, oInfoSet;
                if (owningPlayerId == CompleteInformationState.PLAYER_X) {
                    xInfoSet = myInfoSet;
                    oInfoSet = enemyInfoSet;
                } else {
                    xInfoSet = enemyInfoSet;
                    oInfoSet = myInfoSet;
                }

                ICompleteInformationState ret = new CompleteInformationState(xInfoSet, oInfoSet, owningPlayerId);
                // advance iterator state
                boolean done = false;
                // next possibility for revealed fields
                for (int i = revealing - 1; i >= 0; --i) {
                    if (revealed[i] < revealableFields.length - (revealing - i)) {
                        revealed[i]++;
                        resetRevealed(i + 1);
                        done = true;
                        break;
                    }
                }
                if (done) return ret;

                // next possibility for marked fields
                for (int i = marking - 1; i >= 0; --i) {
                    if (marked[i] < freeFields.length - (marking - i)) {
                        marked[i]++;
                        resetMarked(i + 1);
                        done = true;
                        break;
                    }
                }
                if (done) return ret;

                // increase no. of marked fields
                marking++;
                if (marking <= maxOpponentFields) {
                    resetMarked(0);
                } else {
                    hasNext = false;
                }
                return ret;
                /**
                 * each CIS has my info set as this one
                 * Consider:
                 * min-max no of unknown fields marked by opponent -> no of my fields he knows about
                 * if he has not marked max fields, then which of my fields does he know about
                 * order doesnt matter
                 * this is turn n
                 * iterate min-max - i fields to mark, n - i to reveal:
                 *  - run first i unknown fields marked .. last i unknown fields marked
                 *  - for each - run first (n-i) fields revealed .. last (n-i) fields revealed
                 */

            }
        };
    }

    private long computeComparisonKey() {
        long sum = owningPlayerId;
        long mul = 3;
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
