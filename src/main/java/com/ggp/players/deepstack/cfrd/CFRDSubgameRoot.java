package com.ggp.players.deepstack.cfrd;

import com.ggp.*;
import com.ggp.players.deepstack.cfrd.actions.SelectCISAction;
import com.ggp.players.deepstack.cfrd.percepts.ISSelectedPercept;
import com.ggp.players.deepstack.utils.InformationSetRange;

import java.util.*;

public class CFRDSubgameRoot implements ICompleteInformationState {
    private InformationSetRange range;
    private Map<IInformationSet, Double> opponentCFV;
    private int opponentId;

    public CFRDSubgameRoot(InformationSetRange range, Map<IInformationSet, Double> opponentCFV, int opponentId) {
        this.range = range;
        this.opponentCFV = opponentCFV;
        this.opponentId = opponentId;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public int getActingPlayerId() {
        return 0;
    }

    @Override
    public double getPayoff(int player) {
        return 0;
    }

    @Override
    public List<IAction> getLegalActions() {
        ArrayList<IAction> ret = new ArrayList<>(range.size());
        for (Map.Entry<ICompleteInformationState, Double> entry: range.getProbabilities()) {
            ret.add(new SelectCISAction(entry.getKey(), entry.getValue()/range.getNorm()));
        }
        return ret;
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        return new IInformationSet() {
            private int owner = player;
            @Override
            public IInformationSet next(IAction a) {
                return null;
            }

            @Override
            public IInformationSet applyPercept(IPercept p) {
                if (!isValid(p)) return null;
                ISSelectedPercept per = (ISSelectedPercept) p;
                if (owner != opponentId) {
                    return per.getInformationSet();
                } else {
                    return new OpponentsChoiceIS(opponentId, per.getInformationSet());
                }
            }

            @Override
            public List<IAction> getLegalActions() {
                return null;
            }

            @Override
            public boolean isLegal(IAction a) {
                return false;
            }

            @Override
            public boolean isValid(IPercept p) {
                if (p == null || p.getClass() != ISSelectedPercept.class) return false;
                ISSelectedPercept per = (ISSelectedPercept) p;
                if (per.getInformationSet().getOwnerId() != owner) return false;
                return true;
            }

            @Override
            public int getOwnerId() {
                return owner;
            }
        };
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        if (!isLegal(a)) return null;
        SelectCISAction sel = (SelectCISAction) a;
        ICompleteInformationState s = sel.getSelectedState();
        // TODO: improve this
        double isReachProb = 0d;
        IInformationSet is = s.getInfoSetForPlayer(opponentId);
        for (Map.Entry<ICompleteInformationState, Double> entry: range.getProbabilities()) {
            if (is.equals(entry.getKey().getInfoSetForPlayer(opponentId))) isReachProb += entry.getValue();
        }
        return new OpponentsChoiceState(s, opponentId, opponentCFV.get(s.getInfoSetForPlayer(opponentId))/isReachProb);
    }

    @Override
    public Iterable<IPercept> getPercepts(IAction a) {
        if (!isLegal(a)) return null;
        SelectCISAction sel = (SelectCISAction) a;
        ICompleteInformationState s = sel.getSelectedState();
        return Arrays.asList(new ISSelectedPercept(1, s.getInfoSetForPlayer(1)),
                new ISSelectedPercept(2, s.getInfoSetForPlayer(2)));
    }

    @Override
    public IRandomNode getRandomNode() {
        return new IRandomNode() {
            @Override
            public double getActionProb(IAction a) {
                if (a == null || a.getClass() != SelectCISAction.class) return 0;
                SelectCISAction sel = (SelectCISAction) a;
                return sel.getProb();
            }

            @Override
            public Iterator<IRandomNodeAction> iterator() {
                return new Iterator<IRandomNodeAction>() {
                    private List<IAction> legalActions = getLegalActions();
                    private int idx = 0;
                    @Override
                    public boolean hasNext() {
                        return idx < range.size();
                    }

                    @Override
                    public IRandomNodeAction next() {
                        return (SelectCISAction) legalActions.get(idx++);
                    }
                };
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CFRDSubgameRoot that = (CFRDSubgameRoot) o;
        return opponentId == that.opponentId &&
                Objects.equals(range, that.range) &&
                Objects.equals(opponentCFV, that.opponentCFV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, opponentCFV, opponentId);
    }
}
