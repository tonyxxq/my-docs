package com.ggp.games.LeducPoker;

import com.ggp.games.LeducPoker.actions.CallAction;
import com.ggp.games.LeducPoker.actions.DealCardAction;
import com.ggp.games.LeducPoker.actions.FoldAction;
import com.ggp.utils.GameUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompleteInformationStateTest {
    @Test
    void testFold() {
        CompleteInformationState initialState = (CompleteInformationState) new GameDescription(7, 7).getInitialState();
        CompleteInformationState round1Fold1 = (CompleteInformationState) GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                FoldAction.instance
        );
        assertEquals(-1d, round1Fold1.getPayoff(1));

        CompleteInformationState round2Winning1Fold1 = (CompleteInformationState) GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                CallAction.instance,
                CallAction.instance,
                new DealCardAction(Cards.J),
                FoldAction.instance
        );
        assertEquals(-1d, round2Winning1Fold1.getPayoff(1));

        CompleteInformationState round2Winning1Fold2 = (CompleteInformationState) GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                CallAction.instance,
                CallAction.instance,
                new DealCardAction(Cards.J),
                CallAction.instance,
                FoldAction.instance
        );
        assertEquals(1d, round2Winning1Fold2.getPayoff(1));

        CompleteInformationState round2Winning2Fold1 = (CompleteInformationState) GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                CallAction.instance,
                CallAction.instance,
                new DealCardAction(Cards.Q),
                FoldAction.instance
        );
        assertEquals(-1d, round2Winning2Fold1.getPayoff(1));

        CompleteInformationState round2Winning2Fold2 = (CompleteInformationState) GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                CallAction.instance,
                CallAction.instance,
                new DealCardAction(Cards.Q),
                CallAction.instance,
                FoldAction.instance
        );
        assertEquals(1d, round2Winning2Fold2.getPayoff(1));
    }
}