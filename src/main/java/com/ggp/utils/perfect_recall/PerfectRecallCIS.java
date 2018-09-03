package com.ggp.utils.perfect_recall;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.utils.CompleteInformationStateWrapper;

class PerfectRecallCIS extends CompleteInformationStateWrapper {
    private PerfectRecallIS player1IS, player2IS;

    public PerfectRecallCIS(ICompleteInformationState state, PerfectRecallIS player1IS, PerfectRecallIS player2IS) {
        super(state);
        this.player1IS = player1IS;
        this.player2IS = player2IS;
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        if (!isLegal(a)) return null;
        PerfectRecallIS p1 = player1IS, p2 = player2IS;
        if (state.getActingPlayerId() == 1) {
            p1 = (PerfectRecallIS) p1.next(a);
        } else if (state.getActingPlayerId() == 2) {
            p2 = (PerfectRecallIS) p2.next(a);
        }

        for (IPercept p: state.getPercepts(a)) {
            if (p.getTargetPlayer() == 1) {
                p1 = (PerfectRecallIS) p1.applyPercept(p);
            } else if (p.getTargetPlayer() == 2) {
                p2 = (PerfectRecallIS) p2.applyPercept(p);
            }
        }

        return new PerfectRecallCIS(state.next(a), p1, p2);
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        if (player == 1) return player1IS;
        if (player == 2) return player2IS;
        return null;
    }
}
