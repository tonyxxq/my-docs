package com.ggp.games.RockPaperScissors;

import com.ggp.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CompleteInformationState implements ICompleteInformationState {
    private static final long serialVersionUID = 1L;
    private InformationSet player1IS, player2IS;

    public CompleteInformationState(InformationSet player1IS, InformationSet player2IS) {
        this.player1IS = player1IS;
        this.player2IS = player2IS;
    }

    @Override
    public boolean isTerminal() {
        return player2IS.getChosenAction() != null;
    }

    @Override
    public int getActingPlayerId() {
        if (player1IS.getChosenAction() == null) return 1;
        if (player2IS.getChosenAction() == null) return 2;
        return 0;
    }

    @Override
    public double getPayoff(int player) {
        if (!isTerminal() || player < 1 || player > 2) return 0;
        int p1 = player1IS.getChosenAction().getChosen();
        int p2 = player2IS.getChosenAction().getChosen();
        if (p1 == p2) return 0;
        int size = player1IS.getSize();
        int lookahead = (size-1)/2;
        int winner = 1;
        if ((p1 < p2 && p2 - p1 > lookahead) || (p2 < p1 && p1 - p2 <= lookahead)) {
            winner = 2;
        }
        if (player == winner) return 1;
        return -1;
    }

    @Override
    public List<IAction> getLegalActions() {
        if (isTerminal()) return null;
        return getInfoSetForActingPlayer().getLegalActions();
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        if (player == 1) return player1IS;
        if (player == 2) return player2IS;
        return null;
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        if (!isLegal(a)) return null;
        InformationSet p1 = player1IS, p2 = player2IS;
        if (getActingPlayerId() == 1) {
            p1 = (InformationSet) p1.next(a);
        } else {
            p2 = (InformationSet) p2.next(a);
        }
        return new CompleteInformationState(p1, p2);
    }

    @Override
    public Iterable<IPercept> getPercepts(IAction a) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompleteInformationState that = (CompleteInformationState) o;
        return Objects.equals(player1IS, that.player1IS) &&
                Objects.equals(player2IS, that.player2IS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1IS, player2IS);
    }

    @Override
    public IRandomNode getRandomNode() {
        return null;
    }
}
