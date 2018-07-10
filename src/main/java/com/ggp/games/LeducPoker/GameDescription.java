package com.ggp.games.LeducPoker;

import com.ggp.ICompleteInformationState;
import com.ggp.ICompleteInformationStateFactory;
import com.ggp.IGameDescription;
import com.ggp.IInformationSet;

public class GameDescription implements IGameDescription {
    private final int startingMoney1, startingMoney2;
    private final CompleteInformationState initialState;

    public GameDescription(int startingMoney1, int startingMoney2) {
        this.startingMoney1 = startingMoney1;
        this.startingMoney2 = startingMoney2;
        InformationSet player1IS = new InformationSet(1, null, null, 2, startingMoney1 - 1, startingMoney1, Rounds.PrivateCard, false, 0);
        InformationSet player2IS = new InformationSet(2, null, null, 2, startingMoney2 - 1, startingMoney2, Rounds.PrivateCard, false, 0);
        initialState = new CompleteInformationState(player1IS, player2IS, 0);
    }

    @Override
    public ICompleteInformationState getInitialState() {
        return initialState;
    }

    private boolean isCompatible(IInformationSet is1, IInformationSet is2, int actingPlayer) {
        if (actingPlayer < 0 || actingPlayer > 2) return false;
        if (is1 == null || is2 == null
                || is1.getClass() != InformationSet.class || is2.getClass() != InformationSet.class) return false;
        InformationSet p1 = (InformationSet) is1, p2 = (InformationSet) is2;
        if (p1.getPublicCard() != p2.getPublicCard() || p1.getPotSize() != p2.getPotSize()
                || p1.wasRaised() != p2.wasRaised() || p1.getOwner() != 1 || p2.getOwner() != 2
                || p1.getFoldedByPlayer() != p2.getFoldedByPlayer()) return false;
        if (p1.getPrivateCard() != null && p2.getPrivateCard() != null
                && p1.getPublicCard() != null && p1.getPrivateCard() == p2.getPrivateCard()
                && p1.getPrivateCard() == p1.getPublicCard()) return false;

        // rounds can only differ when player 1 already has private card while player 2 doesn't
        if (p1.getRound() != p2.getRound()
                && (p2.getRound() != Rounds.PrivateCard || p1.getRound() != Rounds.Bet1 || actingPlayer != 0)) return false;
        switch (p2.getRound()) {
            case Bet1:
            case Bet2:
                if (actingPlayer == 0) return false;
                break;
            case PrivateCard:
            case PublicCard:
                if (actingPlayer != 0) return false;
                break;
        }

        return true;
    }

    @Override
    public ICompleteInformationStateFactory getCISFactory() {
        return new ICompleteInformationStateFactory() {
            @Override
            public ICompleteInformationState make(IInformationSet player1, IInformationSet player2, int actingPlayer) {
                if (!isCompatible(player1, player2, actingPlayer)) return null;
                return new CompleteInformationState((InformationSet) player1, (InformationSet) player2, actingPlayer);
            }
        };
    }
}
